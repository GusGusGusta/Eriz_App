package com.example.eriz_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Formulario : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var autoCompleteComuna: AutoCompleteTextView
    private lateinit var editTextObservacion: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var titu_name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formulario_main)

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
            editTextObservacion.requestFocus()
        }

        // Obtener datos pasados desde Menu
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email") // Obtener el email
        val nombre = intent.getStringExtra("nombre") // Obtener el nombre
        val apellido = intent.getStringExtra("apellido") // Obtener apellido
        val comuna = intent.getStringExtra("comuna") // Obtener comuna

        // Rellenar los campos de texto
        editTextNombre.setText(nombre ?: "") // Aquí asignamos el nombre al campo
        editTextApellido.setText(apellido ?: "") // Asignar el apellido
        autoCompleteComuna.setText(comuna ?: "") // Asignar la comuna
        titu_name.text = "Bienvenido, ${username?.uppercase() ?: ""}"

        buttonEnviar.setOnClickListener {
            if (editTextNombre.text.isNullOrEmpty() ||
                editTextApellido.text.isNullOrEmpty() ||
                editTextObservacion.text.isNullOrEmpty()) {

                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show()
            } else {
                val nombre = editTextNombre.text.toString()
                val apellido = editTextApellido.text.toString()
                val comuna = autoCompleteComuna.text.toString()
                val observacion = editTextObservacion.text.toString()

                val asunto = "Eva 1"
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
    }
}
