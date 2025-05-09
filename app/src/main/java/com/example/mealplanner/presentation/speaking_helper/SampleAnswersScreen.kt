package com.example.mealplanner.presentation.speaking_helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleAnswersScreen(viewModel: SampleAnswersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val bandValues = (5..9).flatMap { listOf(it.toFloat(), it + 0.5f) }
        .filter { it <= 9.0f }

    val parts = listOf(
        "Part 1: Introduction and Interview",
        "Part 2: Long Turn",
        "Part 3: Discussion"
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text("Select Part:")

        ExposedDropdownMenuBox(
            expanded = uiState.isPartMenuExpanded,
            onExpandedChange = { viewModel.updatePartMenuExpanded() },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedPart ?: "---",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Chọn") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isPartMenuExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = uiState.isPartMenuExpanded,
                onDismissRequest = { viewModel.updatePartMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                parts.forEach() { part ->
                    DropdownMenuItem(
                        text = { Text(part) },
                        onClick = {
                            viewModel.selectPart(part)
                            viewModel.updatePartMenuExpanded()
                        }
                    )
                }
            }
        }

        // Topic selection spinner
        Text("Select Topic:")

        ExposedDropdownMenuBox(
            expanded = uiState.isTopicMenuExpanded,
            onExpandedChange = { viewModel.updateTopicMenuExpanded() },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedTopic ?: "---",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Chọn") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isTopicMenuExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = uiState.isTopicMenuExpanded,
                onDismissRequest = { viewModel.updateTopicMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.topics.forEach() { topic ->
                    DropdownMenuItem(
                        text = { Text(topic) },
                        onClick = {
                            viewModel.selectTopic(topic)
                            viewModel.updateTopicMenuExpanded()
                        }
                    )
                }
            }
        }

        Text("Select Band:")

        ExposedDropdownMenuBox(
            expanded = uiState.isBandMenuExpanded,
            onExpandedChange = { viewModel.updateBandMenuExpanded() },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedBand?.toString() ?: "---",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Chọn") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isBandMenuExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = uiState.isBandMenuExpanded,
                onDismissRequest = { viewModel.updateBandMenuExpanded() },
                modifier = Modifier.fillMaxWidth()
            ) {
                bandValues.forEach() { band ->
                    DropdownMenuItem(
                        text = { Text(band.toString()) },
                        onClick = {
                            viewModel.selectBand(band)
                            viewModel.updateBandMenuExpanded()
                        }
                    )
                }
            }
        }

        // Display random question
        Text("Random Question:")

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.questionText, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
            )

            IconButton(
                onClick = { viewModel.fetchRandomQuestion() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }

        LoadingDotsButton(text = "Show model answer",
            isLoading = uiState.isLoading,
            onClick = {
                viewModel.fetchModelAnswer(uiState.questionText, uiState.selectedBand!!, uiState.selectedPart!!)
            })

        // Display model answer
        if (uiState.modelAnswer.isNotEmpty()) {
            Text("Model Answer:")
            Text(text = uiState.modelAnswer)
        }
    }
}