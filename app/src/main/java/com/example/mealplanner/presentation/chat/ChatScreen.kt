package com.example.mealplanner.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavHostController
import com.example.mealplanner.presentation.chat.components.MessageBubble
import com.example.mealplanner.presentation.chat.components.MessageWithStatus
import com.example.mealplanner.presentation.common.GalaxyBackground

@Composable
fun ChatScreen(navController: NavHostController, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var toUser by remember { mutableStateOf("user456") }
    var text by remember { mutableStateOf("") }
    var expandedMessageId by remember { mutableStateOf<String?>(null) }



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
//            LazyColumn(modifier = Modifier.weight(1f)) {
//                items(state.messages) { msg ->
//                    Text("${msg.timestamp} From ${msg.fromUser} to ${msg.toUser} : ${msg.text} ")
//                }
//            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GalaxyBackground()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    state = rememberLazyListState(),
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(state.messages.reversed()) { index, message ->
                        val isCurrentUser = message.fromUser == state.currentUserId
                        val isLastMessageFromCurrentUser = state.messages.lastOrNull()?.fromUser == state.currentUserId
                        //val isLastMessageFromCurrentUser = state.messages.isNotEmpty() && state.messages.last().fromUser == state.currentUserId

                        //Kiem tra xem tin nhan co dang mo rong khong
                        val isExpanded = message.tempId == state.expandedMessageId

                        MessageWithStatus(
                            message = message,
                            isCurrentUser = isCurrentUser,
                            isLastMessageFromCurrentUser = isLastMessageFromCurrentUser,
                            isExpanded = isExpanded,
                            onRetry = { },
                            onClick = {
                                viewModel.toggleExpandedMessageId(message.tempId)

                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
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
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (text.isNotBlank()) {
                                    state.currentUserId?.let {
                                        viewModel.sendMessage(
                                            toUser, text,
                                            state.currentUserId!!
                                        )
                                    }
                                    text = ""
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.Send, contentDescription = null,
                                modifier = Modifier.size(24.dp) // Điều chỉnh kích thước icon
                            )
                        }
                    }
                )

            }
        }
    }
}