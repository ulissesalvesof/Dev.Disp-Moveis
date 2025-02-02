package com.example.investidorapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.investidorapp.model.Investimento
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InvestimentosViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("investimentos")
    private val _investimentos = MutableStateFlow<List<Investimento>>(emptyList())
    val investimentos: StateFlow<List<Investimento>> = _investimentos

    init {
        monitorarAlteracoes()
    }

    private fun monitorarAlteracoes() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                carregarInvestimentos()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                carregarInvestimentos()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                carregarInvestimentos()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro: ${error.message}")
            }
        })
    }

    private fun carregarInvestimentos() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Investimento>()
                snapshot.children.forEach { item ->
                    if (item.exists()) {
                        val nome = item.child("nome").getValue(String::class.java) ?: ""
                        val valor = item.child("valor").getValue(Double::class.java) ?: 0.0
                        lista.add(Investimento(nome, valor))
                        Log.d("FirebaseDebug", "Carregado: Nome=$nome, Valor=$valor") // 🐞 Log para debug
                    }
                }
                _investimentos.value = lista
                Log.d("FirebaseDebug", "Lista final: ${lista.size} itens") // 🐞 Log de tamanho da lista
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro: ${error.message}")
            }
        })
    }

}
