package com.example.investidorapp.ui.theme.view

import com.example.investidorapp.viewmodel.InvestimentosViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investidorapp.model.Investimento

@Composable
fun InvestidorScreen(viewModel: InvestimentosViewModel = viewModel()) {
    val investimentos by viewModel.investimentos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Meus Investimentos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        ListaInvestimentos(investimentos = investimentos)
    }
}

@Composable
private fun ListaInvestimentos(investimentos: List<Investimento>) {
    if (investimentos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum investimento encontrado.")
        }
    } else {
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(investimentos) { investimento ->
                InvestimentoItem(investimento = investimento)
            }
        }
    }
}

@Composable
private fun InvestimentoItem(investimento: Investimento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp) // Correção da elevação
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = investimento.nome,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Valor: R$ ${investimento.valor}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
