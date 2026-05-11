package com.example.spendwise.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.spendwise.ui.theme.SpendWisePrimary
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendwise.viewModel.BudgetViewModel

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
) {
    val vm: BudgetViewModel = viewModel()
    val state = vm.uiState
    val context = LocalContext.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        errorTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedBorderColor = SpendWisePrimary,
        unfocusedBorderColor = Color(0xFFCED4DA),
        focusedLeadingIconColor = Color.LightGray,
        unfocusedLeadingIconColor = Color.LightGray,
        focusedTrailingIconColor = Color.LightGray,
        unfocusedTrailingIconColor = Color.LightGray,
    )

    val remainingBudget by remember(state.monthlyBudgetInput, state.monthlyBudgetValue, state.categories) {
        derivedStateOf { vm.remainingBudget }
    }
    val isOverBudget by remember(state.monthlyBudgetInput, state.monthlyBudgetValue, state.categories) {
        derivedStateOf { vm.isOverBudget }
    }

    LaunchedEffect(state.uid, state.monthKey) {
        vm.refreshUid()
        vm.loadCurrentMonth()
    }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (state.showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { vm.closeAddCategoryDialog() },
            title = { Text("Add category") },
            text = {
                OutlinedTextField(
                    value = state.newCategoryName,
                    onValueChange = { vm.setNewCategoryName(it) },
                    singleLine = true,
                    label = { Text("Category name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.confirmAddCategory()
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        vm.closeAddCategoryDialog()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (state.showOtherConfirmDialog) {
        val remain = remainingBudget
        AlertDialog(
            onDismissRequest = { vm.closeOtherConfirmDialog() },
            title = { Text("Unallocated budget") },
            text = {
                Text("You still have RM $remain. Do you want to save as 'Other'?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.confirmSaveAsOther()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { vm.closeOtherConfirmDialog() }
                ) {
                    Text("Close")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = state.monthTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
        )

        if (state.uid == null) {
            Text(
                text = "Please login to manage budgets.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (state.isLoading) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ManagementCard(
            title = "Overall Monthly Budget",
            icon = Icons.Filled.Payments,
        ) {
            OutlinedTextField(
                value = state.monthlyBudgetInput,
                onValueChange = { v ->
                    vm.setMonthlyBudgetInput(v)
                },
                singleLine = true,
                label = { Text("Monthly budget (RM)",color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    vm.saveMonthlyBudgetAndDistributeDefault()
                },
                enabled = state.uid != null && !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save Monthly Budget")
            }
        }

        ManagementCard(
            title = "Category Budget (Breakdown)",
            icon = Icons.Filled.Category,
        ) {
            state.categories.forEach { (name, amountText) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { v ->
                            vm.setCategoryAmount(name, v)
                        },
                        singleLine = true,
                        label = { Text("RM",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(140.dp),
                        colors = textFieldColors
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { vm.openAddCategoryDialog() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add category",
                        tint = SpendWisePrimary,
                    )
                }
                Text(
                    text = "Add new category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpendWisePrimary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Remaining: RM $remainingBudget",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (remainingBudget >= 0) Color.Black else MaterialTheme.colorScheme.error,
            )
        }

        state.message
            ?.takeUnless { it == "Monthly budget set." }
            ?.let { message ->
                val isSuccessMessage = message == "Budget saved."
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSuccessMessage) Color(0xFF0B3D91) else MaterialTheme.colorScheme.error,
                )
            }

        Button(
            onClick = {
                if (isOverBudget) {
                    Toast
                        .makeText(
                            context,
                            "Category budgets exceed your monthly budget. Please re-enter amounts.",
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                    return@Button
                }

                vm.requestSaveBudget()
            },
            enabled = state.uid != null && !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isSaving) "Saving..." else "Save Budget")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ManagementCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = SpendWisePrimary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
            )
            content()
        }
    }
}
