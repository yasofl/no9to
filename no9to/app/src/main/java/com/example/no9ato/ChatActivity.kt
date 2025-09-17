package com.example.no9ato

import android.app.Activity
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var rvMessages: RecyclerView
    private val messages = ArrayList<Message>()
    private lateinit var adapter: MessagesAdapter

    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnGallery: ImageButton
    private lateinit var btnVoice: ImageButton

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("messages")

        rvMessages = findViewById(R.id.rvMessages)
        rvMessages.layoutManager = LinearLayoutManager(this)
        adapter = MessagesAdapter(messages)
        rvMessages.adapter = adapter

        etMessage = findViewById(R.id.editMessage)
        btnSend = findViewById(R.id.btnSend)
        btnGallery = findViewById(R.id.btnGallery)
        btnVoice = findViewById(R.id.btnVoice)

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val chatId = getChatId()
                sendMessageText(chatId, text)
                etMessage.setText("")
            }
        }

        btnGallery.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            i.type = "image/*"
            startActivityForResult(i, 101)
        }

        btnVoice.setOnClickListener {
            // toggle recording for simplicity: start -> stop
            if (mediaRecorder == null) {
                startRecording()
            } else {
                stopRecordingAndUpload()
            }
        }

        // listen for messages of current chat
        val chatId = getChatId()
        dbRef.child(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(Message::class.java)
                if (m != null) {
                    messages.add(m)
                    adapter.notifyItemInserted(messages.size - 1)
                    rvMessages.scrollToPosition(messages.size - 1)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getChatId(): String {
        // chatId passed via intent or fallback to "global"
        val otherId = intent.getStringExtra("userId") ?: "global"
        val me = auth.currentUser?.uid ?: "me"
        return if (me < otherId) "chat_${me}_$otherId" else "chat_${otherId}_$me"
    }

    private fun sendMessageText(chatId: String, text: String) {
        val messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(chatId)
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)
        val messageId = messagesRef.push().key ?: return
        val message = Message(messageId, auth.currentUser?.uid ?: "", text, "text", System.currentTimeMillis())
        messagesRef.child(messageId).setValue(message)
        val chatUpdate = mapOf("lastMessage" to text, "lastTimestamp" to System.currentTimeMillis(), "participants/${auth.currentUser?.uid}" to true)
        chatsRef.updateChildren(chatUpdate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            uploadImageAndSend(uri)
        }
    }

    private fun uploadImageAndSend(uri: Uri) {
        val chatId = getChatId()
        val storageRef = FirebaseStorage.getInstance().reference.child("chat_images/${System.currentTimeMillis()}")
        val uploadTask = storageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessageWithMedia(chatId, downloadUri.toString(), "image")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRecording() {
        try {
            val file = File(cacheDir, "record_\${System.currentTimeMillis()}.3gp")
            audioFilePath = file.absolutePath
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setOutputFile(audioFilePath)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            Toast.makeText(this, "Recording... Tap again to stop.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
            mediaRecorder = null
        }
    }

    private fun stopRecordingAndUpload() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            val path = audioFilePath ?: return
            val file = Uri.fromFile(File(path))
            uploadAudioAndSend(file)
        } catch (e: Exception) {
            Toast.makeText(this, "Stop failed: ${e.message}", Toast.LENGTH_SHORT).show()
            mediaRecorder = null
        }
    }

    private fun uploadAudioAndSend(uri: Uri) {
        val chatId = getChatId()
        val storageRef = FirebaseStorage.getInstance().reference.child("chat_audio/\${System.currentTimeMillis()}.3gp")
        val uploadTask = storageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessageWithMedia(chatId, downloadUri.toString(), "audio")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload audio: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessageWithMedia(chatId: String, url: String, type: String) {
        val messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(chatId)
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId)
        val messageId = messagesRef.push().key ?: return
        val message = Message(messageId, auth.currentUser?.uid ?: "", url, type, System.currentTimeMillis())
        messagesRef.child(messageId).setValue(message)
        val chatUpdate = mapOf("lastMessage" to (if (type=="text") url else "[$type]"), "lastTimestamp" to System.currentTimeMillis(), "participants/${auth.currentUser?.uid}" to true)
        chatsRef.updateChildren(chatUpdate)
    }
}