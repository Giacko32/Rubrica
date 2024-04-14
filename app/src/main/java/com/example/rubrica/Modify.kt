package com.example.rubrica

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.rubrica.databinding.ActivityModifyBinding
import kotlin.random.Random

class Modify : AppCompatActivity() {

    lateinit var binding: ActivityModifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "DBcontatti"
        ).allowMainThreadQueries().build()
        val DB = db.userDao()

        val extras: Bundle? = intent.extras
        if (extras == null) {
            val intent = Intent(this.applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else {
            val name = extras.getString("name") ?: ""
            val surname = extras.getString("surname") ?: ""
            val phone = extras.getString("phone") ?: ""
            val mail = extras.getString("mail") ?: ""
            binding.namefield.setText(name)
            binding.surnamefield.setText(surname)
            binding.phonefield.setText(phone)
            binding.mailfield.setText(mail)
            var oldcolor = DB.getColor(name, surname);
            binding.immagine.setBackgroundColor(oldcolor)

            binding.immagine.setOnClickListener { v: View ->
                val rnd = Random
                oldcolor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                binding.immagine.setBackgroundColor(oldcolor)
            }

            binding.deletebutton.setOnClickListener { v: View ->
                DB.delete(name, surname, phone)
                db.close()
                val intent = Intent(this.applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            binding.savebutton.setOnClickListener { v: View ->
                val newname = binding.namefield.text.toString().trim()
                val newsurname = binding.surnamefield.text.toString().trim()

                if (newname != "" && newsurname != "") {
                    if (DB.findContact(
                            newname,
                            newsurname
                        ).isEmpty() || (name.equals(newname) && surname.equals(newsurname))
                    ) {
                        DB.delete(name, surname, phone)
                        DB.insertAll(
                            Persona(
                                id = 0,
                                nome = newname,
                                cognome = newsurname,
                                telefono = binding.phonefield.text.toString().trim(),
                                mail = binding.mailfield.text.toString().trim(),
                                color = oldcolor
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

}