package com.example.mealplanner.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var toUser by remember { mutableStateOf("user456") }
    var text by remember { mutableStateOf("") }
    var fromUser by remember { mutableStateOf("user123") }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.messages) { msg ->
                    Text("${msg.timestamp} From ${msg.fromUser} to ${msg.toUser} : ${msg.text} ")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fromUser,
                onValueChange = { fromUser = it },
                label = { Text("Send from") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = toUser,
                onValueChange = { toUser = it },
                label = { Text("Send to") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))


            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        viewModel.setUserId(fromUser)
                        viewModel.sendMessage(toUser, text, fromUser)
                        text = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi")
            }
        }
    }
}