package com.example.eriz_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Menu : AppCompatActivity() {

    private lateinit var buttonModificar: Button
    private lateinit var buttonEliminar: Button
    private lateinit var buttonFormulario: Button
    private lateinit var buttonCerrarSesion: Button
    private lateinit var textViewUsername: TextView  // Agregado para el nombre de usuario
    private lateinit var db: FirebaseFirestore
    private var username: String? = null
    private var email: String? = null // Variable para el email
    private var apellido: String? = null // Variable para el apellido
    private var comuna: String? = null // Variable para la comuna
    private var nombre: String? = null // Variable para el nombre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_main)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Obtener el nombre de usuario, email, nombre, apellido y comuna desde el Intent
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")
        nombre = intent.getStringExtra("nombre") // Agregar el nombre
        apellido = intent.getStringExtra("apellido")
        comuna = intent.getStringExtra("comuna")

        // Inicializar los componentes de la interfaz
        buttonModificar = findViewById(R.id.buttonmodi)
        buttonEliminar = findViewById(R.id.buttonelimi)
        buttonFormulario = findViewById(R.id.buttonformu)
        buttonCerrarSesion = findViewById(R.id.buttonsalir)
        textViewUsername = findViewById(R.id.titu_name) // Inicializar el TextView

        // Mostrar el nombre de usuario en el TextView
        textViewUsername.text = "Bienvenido, $username"

        // Configurar listeners para los botones
        buttonModificar.setOnClickListener {
            val intent = Intent(this, Formulario::class.java).apply {
                putExtra("username", username)
                putExtra("email", email) // Agrega el email aquí
                putExtra("nombre", nombre) // Pasa el nombre
                putExtra("apellido", apellido) // Pasa el apellido
                putExtra("comuna", comuna) // Pasa la comuna
            }
            startActivity(intent)
        }

        buttonFormulario.setOnClickListener {
            val intent = Intent(this, Formulario::class.java).apply {
                putExtra("username", username)
                putExtra("email", email) // Agrega el email aquí
                putExtra("nombre", nombre) // Pasa el nombre
                putExtra("apellido", apellido) // Pasa el apellido
                putExtra("comuna", comuna) // Pasa la comuna
            }
            startActivity(intent)
        }

        buttonEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta?")
                .setPositiveButton("Sí") { dialog, which -> eliminarCuenta() }
                .setNegativeButton("No", null)
                .show()
        }

        buttonCerrarSesion.setOnClickListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun eliminarCuenta() {
        if (username == null) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        // Eliminar el documento del usuario en Firestore
        db.collection("usuarios")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        db.collection("usuarios").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Principal::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al conectar a la base de datos", Toast.LENGTH_SHORT).show()
            }
    }
}
