package com.example.apppost

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apppost.ui.screens.PostScreen
import com.example.apppost.ui.screens.UserScreen
import com.example.apppost.ui.theme.AppPostTheme


// Classe principal da atividade
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppPostTheme {
                MainScreen() // Configura a tela principal da aplicação
            }

        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter") // Suprime aviso sobre padding padrão não utilizado
@Composable
fun MainScreen() {
    // Estado para controlar qual aba está selecionada (0 para "Usuários", 1 para "Posts")
    var selectedTab by remember { mutableStateOf(0) }

    // Scaffold é um layout que fornece uma estrutura básica para a tela, com barra superior e inferior
    Scaffold(
        // Barra superior com título e ícone
        topBar = {
            TopAppBar(
                title = {
                    Row ( verticalAlignment = Alignment.CenterVertically ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 10.dp)
                        )
                        Text("PostAPP")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary, // Cor de fundo
                contentColor = Color.White // Cor do texto e ícones
            )
        },
        // Barra de navegação inferior
        bottomBar = {
            BottomNavigation {
                // Item de navegação para a tela de usuários
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Usuários") }, // Ícone de pessoa
                    label = { Text("Usuários") }, // Rótulo "Usuários"
                    selected = selectedTab == 0, // Define se este item está selecionado
                    onClick = { selectedTab = 0 } // Altera o estado para a aba de "Usuários"
                )
                // Item de navegação para a tela de posts
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Posts") }, // Ícone de lista
                    label = { Text("Posts") }, // Rótulo "Posts"
                    selected = selectedTab == 1, // Define se este item está selecionado
                    onClick = { selectedTab = 1 } // Altera o estado para a aba de "Posts"
                )
            }
        }
    ) {
        // Exibe a tela correspondente com base na aba selecionada
        when (selectedTab) {
            0 -> UserScreen() // Tela de usuários
            1 -> PostScreen() // Tela de posts
        }
    }
}