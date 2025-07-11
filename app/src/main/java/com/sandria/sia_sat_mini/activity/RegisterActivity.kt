package com.sandria.sia_sat_mini.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val roles = resources.getStringArray(R.array.user_roles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRole.adapter = adapter

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

        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val role = binding.spinnerRole.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && nama.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val uid = it.user?.uid
                        val userData = hashMapOf(
                            "nama" to nama,
                            "email" to email,
                            "role" to role
                        )
                        // Simpan ke Firestore di koleksi "users"
                        db.collection("users").document(uid!!).set(userData)
                            .addOnSuccessListener {
                                AlertDialog.Builder(this)
                                    .setTitle("Registrasi Berhasil")
                                    .setMessage("Akun berhasil dibuat. Silakan login.")
                                    .setCancelable(false)
                                    .setPositiveButton("Kembali") { _, _ ->
                                        val intent = Intent(this, WelcomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Register gagal: ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "Isi semua field & password minimal 6 karakter",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
