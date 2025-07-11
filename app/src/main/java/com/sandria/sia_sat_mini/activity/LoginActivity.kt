package com.sandria.sia_sat_mini.activity

import android.os.Bundle
import android.content.Intent
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        var isPasswordVisible = false

        binding.etPassword.setOnTouchListener { v, event ->
            val drawableEnd = 2 // index 2 = drawableEnd
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.etPassword.compoundDrawables[drawableEnd]
                if (drawable != null && event.rawX >= (binding.etPassword.right - drawable.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_visibility, 0)
                    } else {
                        binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_visibility_off, 0)
                    }
                    // Move cursor to end
                    binding.etPassword.setSelection(binding.etPassword.text.length)
                    true
                }
            }
            false
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val uid = it.user?.uid
                        db.collection("users").document(uid!!).get()
                            .addOnSuccessListener { doc ->
                                val role = doc.getString("role")
                                val nama = doc.getString("nama") // pastikan field-nya 'name' bukan 'nama'

                                when (role) {
                                    "kaprodi" -> {
                                        val intent = Intent(this, KaprodiActivity::class.java)
                                        intent.putExtra("nama", nama)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "dosen" -> {
                                        val intent = Intent(this, DosenActivity::class.java)
                                        intent.putExtra("nama", nama)
                                        startActivity(intent)
                                    }
                                    "mahasiswa" -> {
                                        val intent = Intent(this, MahasiswaActivity::class.java)
                                        intent.putExtra("nama", nama)
                                        startActivity(intent)
                                    }
                                    else -> {
                                        Toast.makeText(this, "Role tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal ambil data user", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
