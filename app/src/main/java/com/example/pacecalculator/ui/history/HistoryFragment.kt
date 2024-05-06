package com.example.pacecalculator.ui.history

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pacecalculator.databinding.FragmentHistoryBinding
import java.util.ArrayList

class HistoryFragment : Fragment() {

    //Variável para o banco
    private lateinit var databaseApp : SQLiteDatabase

    //Variável captando listview
    private lateinit var historyList : ListView

    //Variável fictícia
    private lateinit var list : java.util.ArrayList<String>

private var _binding: FragmentHistoryBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentHistoryBinding.inflate(inflater, container, false)
    val root: View = binding.root

      historyList = binding.history

      loadDatabase()

    return root
  }

    fun loadDatabase() {
        try {
            //Transformando 
            list = ArrayList()

            databaseApp = requireContext().openOrCreateDatabase("dbPaceCalculator",
                Context.MODE_PRIVATE, null)

            val cursor : Cursor = databaseApp.rawQuery("SELECT pace, time, kilometers FROM calculateResults", null)

            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                list.add("Pace: " + cursor.getString(0) + " Distância: " + cursor.getString(2)
                + " Tempo: " + cursor.getString(1))
                cursor.moveToNext()
            }
            cursor.close()

            //Adaptando conteúdo para listView
            val adapterHistory : ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                list
            )
            historyList.adapter = adapterHistory

        } catch (e : Exception) {
            e.printStackTrace()
        }        }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}