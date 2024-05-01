package com.example.pacecalculator.ui.calculator

import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pacecalculator.R
import com.example.pacecalculator.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    //Criando variável para o banco
    private lateinit var databaseApp : SQLiteDatabase

    //Gerando variáveis para elementos do layout da calculadora
    private lateinit var edtTime: EditText
    private lateinit var edtDistance: EditText
    private lateinit var btnClear: Button
    private lateinit var btnCalculate: Button
    private lateinit var txtSetResult: TextView
    private lateinit var txtResult: TextView
    private lateinit var imgResult: ImageView

    private var _binding: FragmentCalculatorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //Captando as variáveis do layout
        edtTime = binding.edtTime
        edtDistance = binding.edtDistance
        btnClear = binding.btnClear
        btnCalculate = binding.btnCalculate
        txtSetResult = binding.txtSetResult
        txtResult = binding.txtResult
        imgResult = binding.imgResult


        btnCalculate.setOnClickListener(){
            calculatePace()
        }

        //Criando / abrindo o banco
        createDatabase()

        return root
    }

    fun createDatabase() {
        try {
            databaseApp = requireContext().openOrCreateDatabase("dbPaceCalculator", MODE_PRIVATE, null)

            databaseApp.execSQL("CREATE TABLE IF NOT EXISTS calculateResults" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, pace TEXT, time TIME, kilometers FLOAT)")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun save() {
        try {
            val paceDB = txtResult.text.toString()

            val timeDB = getTime()

            val kilometersDB = getDistance()

            //Abrindo o banco
            databaseApp = requireContext().openOrCreateDatabase("dbPaceCalculator", MODE_PRIVATE, null)
            //Fazendo statement para adicionar os dados
            val sql = "INSERT INTO calculateResults (pace, time, kilometers) VALUES (?, ?, ?)"
            val stmt = databaseApp.compileStatement(sql)
            stmt.bindString(1,paceDB)
            stmt.bindDouble(2,timeDB)
            stmt.bindDouble(3,kilometersDB)

            stmt.executeInsert()

            databaseApp.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun calculatePace() {

        //Captando a distância (em Km) da função
        val distance = getDistance()
        //Captando o tempo (em minutos) da função
        val time = getTime()

        //Calculando o pace
        val pace = time / distance


        //Tratando os segundos do pace separado dos minutos
        //totalSeconds calcula o total de segundos multiplicando o pace por 60
        val totalSeconds = (pace * 60).toInt()
        //paceMinutes calcula os minutos dividindo o total de segundos por 60
        val paceMinutes = totalSeconds / 60
        //paceSeconds calcula os segundos restantes usando o operador % (módulo)
        //Assim, os segundos sempre são um valor positivo, entre 0 e 59
        val paceSeconds = totalSeconds % 60
        //Formatando os segundos, para aparecer sempre duas casas no número
        val formattedPaceSeconds = "%02d".format(paceSeconds)


        //Mostrando pace na tela
        txtResult.setText(paceMinutes.toString() + ":" + formattedPaceSeconds + " min/km")


        //Mostrar também a distância mais bonitinho dps de calcular = 0,00 km
        //Mostrar o tempo bonitinho dps de calcular = 0h 00min 00s

        txtSetResult.setText("Seu pace é:")

        imgResult.setImageResource(R.drawable.ic_pace);


        save()
    }

    //Variável para captar e tratar a distância
    fun getDistance(): Double {
        //Captando a distância do layout
        val distanceText = edtDistance.text.toString()
        //Para caso o usuário coloque ',' para separar
        // Substituir ',' por '.' se presente
        val cleanedDistanceText = distanceText.replace(',', '.')
        // Converter para double
        val distance = cleanedDistanceText.toDoubleOrNull() ?: 0.0
        //Retornando para ser usado na função de cálculo
        return distance
    }

    //Função para captar e tratar o tempo
    fun getTime() : Double {
        //Captando o tempo do layout
        val time = edtTime.text.toString()

        //Separando o string em três partes, uma para cada ':'
        val timeUnits = time.split(":")

        //Criando variáveis para cada parte
        var hours = 0.0
        var minutes = 0.0
        var seconds = 0.0

        //Preenchendo as unidades com os dados do usuário
        //Cada parte vira um []
        if (timeUnits.size >= 3) {
            hours = timeUnits[0].toDoubleOrNull() ?: 0.0
            minutes = timeUnits[1].toDoubleOrNull() ?: 0.0
            seconds = timeUnits[2].toDoubleOrNull() ?: 0.0
        }

        //Agora transformando as horas e segundos em minutos,
        // e colocando tudo na variável dos minutos
        //Na hora do cálculo, só vai precisar ser considerado os minutos
        minutes += (hours * 60) + (seconds / 60)

        //Retornando os minutos, para usar na função de calcular o pace
        return minutes
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}