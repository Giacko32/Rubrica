package com.example.rubrica

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rubrica.databinding.ActivityMainBinding


@Entity
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val nome: String,
    @ColumnInfo(name = "surname") val cognome: String,
    @ColumnInfo(name = "email") val mail: String,
    @ColumnInfo(name = "phone") val telefono: String,
    @ColumnInfo(name = "color") val color: Int
)

@Dao
interface PersonaDao {
    @Query("SELECT * FROM Persona ORDER BY name ASC")
    fun getAll(): List<Persona>

    @Insert
    fun insertAll(vararg users: Persona)

    @Query("DELETE FROM Persona WHERE (name = :nomefun AND surname= :cognomefun AND phone = :phonefun)")
    fun delete(nomefun: String, cognomefun: String, phonefun: String): Int

    @Query("SELECT color FROM Persona WHERE (name = :nomefun AND surname= :cognomefun)")
    fun getColor(nomefun: String, cognomefun: String): Int

    @Query("SELECT * FROM Persona WHERE (name = :nomefun AND surname = :cognomefun)")
    fun findContact(nomefun: String, cognomefun: String): List<Persona>

}

@Database(entities = [Persona::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): PersonaDao
}

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    inner class MyAdapter(val dati: List<Persona>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        inner class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
            val testo = row.findViewById<TextView>(R.id.testo)
            val img = row.findViewById<ImageView>(R.id.immagine)
            val button = row.findViewById<ImageView>(R.id.call)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val layout =
                LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
            return MyViewHolder(layout)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.testo.text = "${dati.get(position).nome}  ${dati.get(position).cognome}"
            holder.img.setImageResource(R.drawable.baseline_account_circle_24)
            holder.img.setBackgroundColor(dati.get(position).color)
            holder.button.setOnClickListener { v: View ->
                val intentcall = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:" + dati.get(position).telefono)
                }
                if (intentcall.resolveActivity(packageManager) != null) {
                    startActivity(intentcall)
                }
            }

            holder.row.setOnClickListener { v: View ->
                val intentmod = Intent(applicationContext, Modify::class.java)
                intentmod.putExtra("name", dati.get(position).nome)
                intentmod.putExtra("surname", dati.get(position).cognome)
                intentmod.putExtra("phone", dati.get(position).telefono)
                intentmod.putExtra("mail", dati.get(position).mail)
                startActivity(intentmod)
            }
        }

        override fun getItemCount(): Int = dati.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "DBcontatti"
        ).allowMainThreadQueries().build()

        val DB = db.userDao()

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = MyAdapter(DB.getAll())
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                this.applicationContext,
                LinearLayoutManager.VERTICAL
            )
        )

        db.close()

        binding.addbutton.setOnClickListener { v: View ->
            val intent = Intent(this.applicationContext, Add::class.java)
            startActivity(intent)
        }

    }


}