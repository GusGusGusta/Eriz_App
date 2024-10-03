package com.example.eriz_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class F_actualizar : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var autoCompleteComuna: AutoCompleteTextView
    private lateinit var editTextObservacion: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var titu_name: TextView

    private lateinit var db: FirebaseFirestore
    private var username: String? = null
    private var isModificar: Boolean = false // Indicador de acción

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de tener un layout llamado activity_main2.xml
        setContentView(R.layout.formulario_main)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Inicializar los elementos de la interfaz
        titu_name = findViewById(R.id.titu_name)
        editTextNombre = findViewById(R.id.edit_nombre)
        editTextApellido = findViewById(R.id.edit_apellido)
        autoCompleteComuna = findViewById(R.id.edit_comuna)
        editTextObservacion = findViewById(R.id.editTextObservacion)
        buttonEnviar = findViewById(R.id.buttonEnviar)

        // Configuración para el AutoCompleteTextView
        val comunas = resources.getStringArray(R.array.comunas_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, comunas)
        autoCompleteComuna.setAdapter(adapter)

        autoCompleteComuna.setOnItemClickListener { _, _, _, _ ->
            // Mueve el enfoque al campo de observación cuando se selecciona una comuna
            editTextObservacion.requestFocus()
        }

        // Obtener el nombre de usuario pasado desde Menu
        username = intent.getStringExtra("username")
        titu_name.text = "Actualizacion de " + (username?.uppercase() ?: "")

        // Determinar la acción: modificar cuenta o enviar formulario
        // Puedes pasar un extra en el Intent para diferenciar
        isModificar = intent.getBooleanExtra("isModificar", false)

        if (isModificar && username != null) {
            cargarDatosUsuario(username!!)
        }

        buttonEnviar.setOnClickListener {
            if (editTextNombre.text.isNullOrEmpty() ||
                editTextApellido.text.isNullOrEmpty() ||
                editTextObservacion.text.isNullOrEmpty()) {

                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            } else {
                if (isModificar) {
                    modificarCuenta()
                } else {
                    enviarFormulario()
                }
            }
        }
    }

    private fun cargarDatosUsuario(username: String) {
        // Cargar los datos del usuario desde Firestore para modificarlos
        db.collection("usuarios")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    editTextNombre.setText(document.getString("nombre") ?: "")
                    editTextApellido.setText(document.getString("apellido") ?: "")
                    autoCompleteComuna.setText(document.getString("comuna") ?: "")
                    editTextObservacion.setText(document.getString("observacion") ?: "")
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun modificarCuenta() {
        if (username == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val comuna = autoCompleteComuna.text.toString()
        val observacion = editTextObservacion.text.toString()

        // Actualizar los datos en Firestore
        db.collection("usuarios")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        db.collection("usuarios").document(document.id)
                            .update(
                                mapOf(
                                    "nombre" to nombre,
                                    "apellido" to apellido,
                                    "comuna" to comuna,
                                    "observacion" to observacion
                                )
                            )
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cuenta actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                finish() // Regresar a Menu
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar la cuenta", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Cuenta no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enviarFormulario() {
        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val comuna = autoCompleteComuna.text.toString()
        val observacion = editTextObservacion.text.toString()

        val asunto = "Formulario de " + (username ?: "Usuario")
        val cuerpo = "Nombre: $nombre\nApellido: $apellido\nComuna: $comuna\nObservación: $observacion"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("destinatario@example.com")) // Cambiar destinatario
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo usando:"))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No hay ninguna aplicación de correo instalada.", Toast.LENGTH_LONG).show()
        }
    }
}
