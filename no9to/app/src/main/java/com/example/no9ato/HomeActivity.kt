package com.example.no9ato

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerUsers: RecyclerView
    private lateinit var btnProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerUsers = findViewById(R.id.recyclerUsers)
        btnProfile = findViewById(R.id.btnProfile)

        // RecyclerView بسيط دون بيانات حقيقية لتفادي أخطاء وقت التشغيل
        recyclerUsers.layoutManager = LinearLayoutManager(this)
        val emptyList = listOf<com.example.no9ato.User>()
        recyclerUsers.adapter = UsersAdapter(emptyList) { user ->
            // عند الضغط على مستخدم ننتقل إلى شاشة الدردشة (يمكن تعديل لاحقًا لتمرير بيانات المستخدم)
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // التحقق من تسجيل الدخول، وإلا نعيد المستخدم إلى صفحة تسجيل الدخول
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
