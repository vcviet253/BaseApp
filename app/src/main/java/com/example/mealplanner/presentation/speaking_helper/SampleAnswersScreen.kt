package com.example.mealplanner.presentation.speaking_helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mealplanner.presentation.speaking_helper.components.LoadingDotsButton

@Composable
fun SampleAnswersScreen(viewModel: SampleAnswersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val bandValues = (5..9).flatMap { listOf(it.toFloat(), it + 0.5f) }
        .filter { it <= 9.0f }


    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        // Topic selection spinner
        Text("Select Topic:")

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.updateTopicMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = uiState.selectedTopic ?: "Select a topic")
            }
            DropdownMenu(
                expanded = uiState.isTopicMenuExpanded,
                onDismissRequest = { viewModel.updateTopicMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.topics.forEach { topic ->
                    DropdownMenuItem(
                        text = { Text(text = topic) },
                        onClick = {
                            viewModel.selectTopic(topic)
                            viewModel.updateTopicMenuExpanded()
                        }
                    )
                }
            }
        }

        Text("Select Band:")
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.updateBandMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = uiState.selectedBand.toString())
            }
            DropdownMenu(
                expanded = uiState.isBandMenuExpanded,
                onDismissRequest = { viewModel.updateBandMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                bandValues.forEach { band ->
                    DropdownMenuItem(
                        text = { Text(text = band.toString()) },
                        onClick = {
                            viewModel.selectBand(band)
                            viewModel.updateBandMenuExpanded()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Display random question
        Text("Random Question:")

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = uiState.questionText, modifier = Modifier.padding(vertical = 8.dp).weight(1f))

            IconButton(
                onClick = { viewModel.fetchRandomQuestion() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoadingDotsButton(text = "Show model answer",
            isLoading = uiState.isLoading,
            onClick = {
                viewModel.fetchModelAnswer(uiState.questionText, uiState.selectedBand)
            })

        Spacer(modifier = Modifier.height(16.dp))

        // Display model answer
        if (uiState.modelAnswer.isNotEmpty()) {
            Text("Model Answer:")
            Text(text = uiState.modelAnswer)
        }
    }
}