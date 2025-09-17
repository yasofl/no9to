package com.example.no9ato

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var ivAvatar: ImageView
    private lateinit var etName: EditText
    private lateinit var btnSave: Button
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        ivAvatar = findViewById(R.id.ivProfileAvatar)
        etName = findViewById(R.id.etProfileName)
        btnSave = findViewById(R.id.btnSaveProfile)

        ivAvatar.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i, 201)
        }

        val uid = auth.currentUser?.uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.get().addOnSuccessListener { snap ->
            val user = snap.getValue(User::class.java)
            user?.let {
                etName.setText(it.name)
            }
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedImageUri != null) {
                uploadProfileImageAndSave(name, selectedImageUri!!)
            } else {
                saveUserProfile(name, "")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            ivAvatar.setImageURI(selectedImageUri)
        }
    }

    private fun uploadProfileImageAndSave(name: String, uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid/\${System.currentTimeMillis()}")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                saveUserProfile(name, downloadUri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserProfile(name: String, imageUrl: String) {
        val uid = auth.currentUser?.uid ?: return
        val user = User(uid, name, auth.currentUser?.email ?: "", imageUrl)
        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(user)
            .addOnSuccessListener { Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
    }
}