package com.example.eriz_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class Principal : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var togglePasswordVisibility: ImageView
    private lateinit var textCrear: TextView
    private var isPasswordVisible: Boolean = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        togglePasswordVisibility = findViewById(R.id.toggle_password_visibility)
        textCrear = findViewById(R.id.textcrear)

        db = FirebaseFirestore.getInstance()

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty()) {
                emailInput.error = "El email es obligatorio"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "La contraseña es obligatoria"
                return@setOnClickListener
            }

            Toast.makeText(this, "Validando usuario...", Toast.LENGTH_SHORT).show()

            db.collection("usuarios")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userDocument = documents.first()
                        val username = userDocument.getString("username")
                        val nombre = userDocument.getString("nombre")
                        val apellido = userDocument.getString("apellido")
                        val comuna = userDocument.getString("comuna")

                        Toast.makeText(this, "Hola, $username", Toast.LENGTH_LONG).show()
                        Log.i("Firestore", "Usuario autenticado: $username")

                        // Cambia aquí para no pasar la contraseña
                        val intent = Intent(this, Menu::class.java).apply {
                            putExtra("username", username)
                            putExtra("nombre", nombre)
                            putExtra("apellido", apellido)
                            putExtra("comuna", comuna)
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        Log.w("Firestore", "Credenciales inválidas para el usuario: $email")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Error al consultar Firestore", exception)
                }
        }

        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType = if (isPasswordVisible) {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            togglePasswordVisibility.setImageResource(if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed)
            passwordInput.setSelection(passwordInput.text.length)
        }

        textCrear.setOnClickListener {
            val intent = Intent(this, F_ingresar::class.java)
            startActivity(intent)
        }
    }
}
