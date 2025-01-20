package com.example.apppost.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apppost.data.model.Post
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PostItem(
    post: Post,
    onDelete: (Int) -> Unit,
    onEdit: (Post) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.body1)
            Text(text = post.content, style = MaterialTheme.typography.body2)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Button(
                    onClick = {showDialog = true},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Deletar")
                }

                Button(
                    onClick = {onEdit(post)},
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.onPrimary),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Editar")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text( text = "Confirmar exclusão")},
                    text = { Text(text = "Tem certeza que deseja excluir este post?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onDelete(post.id)
                            showDialog = false
                        }) {
                            Text(text = "Sim")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog = false
                        }) {
                            Text(text = "Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PostItemPreview() {
    val post = Post(id = 1, title = "Testando", content = "1, 2, 3 testando", owner_id = 1)

    PostItem(
        post = post,
        onDelete = { id ->
            println("Post com ID $id excluído")
        },
        onEdit = { updatedPost ->
            println("Editar post: $updatedPost")
        }
    )
}
