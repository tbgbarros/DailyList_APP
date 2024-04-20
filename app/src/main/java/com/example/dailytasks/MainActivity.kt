package com.example.dailytasks

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var listaTarefas: ListView
    private lateinit var textVazio: TextView
    private lateinit var botaoTarefas: Button
    private lateinit var tarefas: ArrayList<String>
    private lateinit var adaptador: ArrayAdapter<String>
    private lateinit var banco: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        banco = getSharedPreferences("MinhaLista", Context.MODE_PRIVATE)
        tarefas = carregarTarefas()
        adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, tarefas)


        listaTarefas = findViewById(R.id.listaTarefas)
        listaTarefas.adapter = adaptador

        textVazio = findViewById(R.id.textVazio)
        botaoTarefas = findViewById(R.id.botaoTarefas)

        botaoTarefas.setOnClickListener {
            adicionarTarefa()
        }
        atualizarLista()

        listaTarefas.setOnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(this)
                .setMessage("Deseja excluir esta tarefa?")
                .setPositiveButton("Sim") { dialog, which ->
                    // Remover a tarefa da lista
                    tarefas.removeAt(position)
                    // Salvar as tarefas atualizadas
                    salvarTarefas()
                    // Notificar o adaptador de que os dados mudaram
                    adaptador.notifyDataSetChanged()
                    // Atualizar a exibição da lista
                    atualizarLista()
                }
                .setNegativeButton("Não") { dialog, which ->
                    // Cancelar a exclusão
                    dialog.dismiss()
                }
                .show()

            // Retorna true para indicar que o evento de clique longo foi tratado
            true
        }
    }


    private fun carregarTarefas(): ArrayList<String> {
        val gson = Gson()
        val json = banco.getString("Tarefas", null)
        return if (json != null) {
            gson.fromJson(json, Array<String>::class.java).toCollection(ArrayList())
        } else {
            ArrayList()
        }
    }

    private fun salvarTarefas() {
        val gson = Gson()
        val json = gson.toJson(tarefas)
        banco.edit().putString("Tarefas", json).apply()
    }

    private fun adicionarTarefa() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nova Tarefa")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Adicionar") { _, _ ->
            val novaTarefa = input.text.toString()
            if (novaTarefa.isNotEmpty()) {
                tarefas.add(novaTarefa)
                salvarTarefas()
                adaptador.notifyDataSetChanged()
                atualizarLista()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }


    private fun atualizarLista(){
        if (tarefas.isEmpty()){
            listaTarefas.visibility = View.GONE
            textVazio.visibility = View.VISIBLE
        } else {
            listaTarefas.visibility = View.VISIBLE
            textVazio.visibility = View.GONE
        }
    }


}