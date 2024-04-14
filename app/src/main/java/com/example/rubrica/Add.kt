package com.example.rubrica

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.rubrica.databinding.ActivityAddBinding
import kotlin.random.Random

class Add : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "DBcontatti"
        ).allowMainThreadQueries().build()

        val DB = db.userDao()

        val rnd = Random
        var rndcolor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        binding.immagine.setBackgroundColor(rndcolor)

        binding.immagine.setOnClickListener { v: View ->
            rndcolor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            binding.immagine.setBackgroundColor(rndcolor)
        }

        binding.savebutton.setOnClickListener { v: View ->
            if (binding.namefield.text.toString() != "" && binding.surnamefield.text.toString() != "") {
                if (DB.findContact(
                        binding.namefield.text.toString().trim(),
                        binding.surnamefield.text.toString().trim()
                    ).isEmpty()
                ) {
                    DB.insertAll(
                        Persona(
                            id = 0,
                            nome = binding.namefield.text.toString().trim(),
                            cognome = binding.surnamefield.text.toString().trim(),
                            telefono = binding.phonefield.text.toString().trim(),
                            mail = binding.mailfield.text.toString().trim(),
                            color = rndcolor
                        )
                    )

                    db.close()

                    val intent = Intent(this.applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val alertDialog = AlertDialog.Builder(
                        this,
                        androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog_Alert
                    ).create()
                    alertDialog.setTitle("Attenzione")
                    alertDialog.setMessage("Esiste già un contatto con questo nome e questo cognome")
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEGATIVE, "MODIFICA"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
            }
        }
    }
}