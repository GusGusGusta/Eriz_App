package com.example.eriz_app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class F_ingresar : AppCompatActivity() {

    // Declaración de variables para los elementos de la interfaz
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var autoCompleteComuna: AutoCompleteTextView
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var tituName: TextView

    // Instancia de Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.f_ingresar_main)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Inicializar los elementos de la interfaz
        tituName = findViewById(R.id.titu_name)
        editTextNombre = findViewById(R.id.edit_nombre)
        editTextApellido = findViewById(R.id.edit_apellido)
        editTextUsername = findViewById(R.id.edit_apodo)
        editTextEmail = findViewById(R.id.edit_email)
        autoCompleteComuna = findViewById(R.id.edit_comuna)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonEnviar = findViewById(R.id.buttonEnviar)

        // Configuración para el AutoCompleteTextView
        val comunas = resources.getStringArray(R.array.comunas_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, comunas)
        autoCompleteComuna.setAdapter(adapter)

        autoCompleteComuna.setOnItemClickListener { _, _, _, _ ->
            // Mueve el enfoque al campo de contraseña cuando se selecciona una comuna
            editTextPassword.requestFocus()
        }

        // Configurar el título
        tituName.text = "Creación de Cuenta"

        // Configurar el listener para el botón de enviar
        buttonEnviar.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val apellido = editTextApellido.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val comuna = autoCompleteComuna.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            // Validar campos
            if (nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() ||
                email.isEmpty() || comuna.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar formato de email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.error = "Por favor, ingrese un email válido."
                return@setOnClickListener
            }

            // Validar que las contraseñas coincidan
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar la fortaleza de la contraseña (mínimo 6 caracteres)
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Crear un nuevo usuario en Firestore
            val nuevoUsuario = hashMapOf(
                "username" to username.lowercase(), // Asegura que el username sea en minúsculas
                "nombre" to nombre,
                "apellido" to apellido,
                "email" to email,
                "comuna" to comuna,
                "password" to password // **Nota:** Es recomendable encriptar la contraseña antes de almacenarla
            )

            // Verificar que el username no exista ya
            db.collection("usuarios")
                .whereEqualTo("username", nuevoUsuario["username"])
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "El nombre de usuario ya está en uso. Por favor, elige otro.", Toast.LENGTH_LONG).show()
                    } else {
                        // Verificar que el email no exista ya
                        db.collection("usuarios")
                            .whereEqualTo("email", nuevoUsuario["email"])
                            .get()
                            .addOnSuccessListener { emailDocuments ->
                                if (!emailDocuments.isEmpty) {
                                    Toast.makeText(this, "El email ya está en uso. Por favor, usa otro.", Toast.LENGTH_LONG).show()
                                } else {
                                    // Agregar el nuevo usuario
                                    db.collection("usuarios")
                                        .add(nuevoUsuario)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Cuenta creada exitosamente.", Toast.LENGTH_LONG).show()
                                            // Regresar a la actividad principal
                                            val intent = Intent(this, Principal::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al registrar el usuario. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al verificar el email. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al conectar con la base de datos. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                }
        }
    }
}
