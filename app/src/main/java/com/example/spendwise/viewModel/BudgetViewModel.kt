package com.example.spendwise.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.floor

data class BudgetUiState(
    val uid: String? = null,
    val monthKey: String = "",
    val monthTitle: String = "",
    val monthlyBudgetInput: String = "",
    val monthlyBudgetValue: Long? = null,
    val categories: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val message: String? = null,
    val showAddCategoryDialog: Boolean = false,
    val newCategoryName: String = "",
    val showOtherConfirmDialog: Boolean = false,
    val isEditCategoriesMode: Boolean = false,
)

class BudgetViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val defaultCategories = listOf("Food", "Shopping", "Transport", "Bills")

    var uiState by mutableStateOf(
        BudgetUiState(
            uid = auth.currentUser?.uid,
            monthKey = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")),
            monthTitle = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            categories = defaultCategories.associateWith { "" },
        )
    )
        private set

    val totalCategoryBudget: Long
        get() = uiState.categories.values.sumOf { it.toLongOrNull() ?: 0L }

    val remainingBudget: Long
        get() {
            val monthly = uiState.monthlyBudgetValue ?: (uiState.monthlyBudgetInput.toLongOrNull() ?: 0L)
            return monthly - totalCategoryBudget
        }

    val isOverBudget: Boolean
        get() = remainingBudget < 0

    private fun currentMonthKey(): String =
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    private fun currentMonthTitle(): String =
        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    private fun refreshMonthContext() {
        val newKey = currentMonthKey()
        val newTitle = currentMonthTitle()
        if (uiState.monthKey != newKey || uiState.monthTitle != newTitle) {
            uiState = uiState.copy(monthKey = newKey, monthTitle = newTitle)
        }
    }

    fun refreshUid() {
        uiState = uiState.copy(uid = auth.currentUser?.uid)
    }

    fun loadCurrentMonth() {
        refreshMonthContext()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            uiState = uiState.copy(uid = null, message = "Please login to manage budgets.")
            return
        }

        uiState = uiState.copy(uid = uid, isLoading = true, message = null)

        firestore
            .collection("users")
            .document(uid)
            .collection("budget")
            .document(uiState.monthKey)
            .get()
            .addOnSuccessListener { doc ->
                var nextMonthlyInput = ""
                var nextMonthlyValue: Long? = null
                val nextCategories = mutableMapOf<String, String>()

                if (doc.exists()) {
                    val loadedMonthly = doc.getLong("monthlyBudget")
                    val loadedCategories = doc.get("categories") as? Map<*, *>

                    if (loadedMonthly != null) {
                        nextMonthlyValue = loadedMonthly
                        nextMonthlyInput = loadedMonthly.toString()
                    }

                    if (loadedCategories != null) {
                        loadedCategories.forEach { (k, v) ->
                            val name = k?.toString()?.trim().orEmpty()
                            val amount = (v as? Number)?.toLong() ?: v?.toString()?.toLongOrNull()
                            if (name.isNotBlank()) {
                                nextCategories[name] = amount?.toString().orEmpty()
                            }
                        }
                    }
                }

                if (nextCategories.isEmpty()) {
                    nextCategories.putAll(defaultCategories.associateWith { "" })
                }

                uiState = uiState.copy(
                    isLoading = false,
                    monthlyBudgetInput = nextMonthlyInput,
                    monthlyBudgetValue = nextMonthlyValue,
                    categories = nextCategories,
                )
            }
            .addOnFailureListener { e ->
                uiState = uiState.copy(isLoading = false, message = e.message ?: "Failed to load budget")
            }
    }

    fun setMonthlyBudgetInput(value: String) {
        uiState = uiState.copy(
            monthlyBudgetInput = value.filter { it.isDigit() },
            monthlyBudgetValue = null,
        )
    }

    fun setCategoryAmount(name: String, value: String) {
        val cleaned = value.filter { it.isDigit() }
        uiState = uiState.copy(categories = uiState.categories.toMutableMap().apply { put(name, cleaned) })
    }
    fun toggleEditCategoriesMode() {
        uiState = uiState.copy(isEditCategoriesMode = !uiState.isEditCategoriesMode)
    }

    fun removeCategory(name: String) {
        val current = uiState.categories
        if (current.size <= 1) {
            uiState = uiState.copy(message = "At least one category is required.")
            return
        }

        val nextCategories = current.toMutableMap().apply { remove(name) }
        uiState = uiState.copy(
            categories = nextCategories,
            message = null,
        )

        if (uiState.uid != null) {
            saveToFirebase(nextCategories)
        }
    }

    fun openAddCategoryDialog() {
        uiState = uiState.copy(showAddCategoryDialog = true, newCategoryName = "")
    }

    fun closeAddCategoryDialog() {
        uiState = uiState.copy(showAddCategoryDialog = false, newCategoryName = "")
    }

    fun setNewCategoryName(value: String) {
        uiState = uiState.copy(newCategoryName = value)
    }

    fun confirmAddCategory() {
        val name = uiState.newCategoryName.trim()
        if (name.isNotBlank() && !uiState.categories.containsKey(name)) {
            uiState = uiState.copy(
                categories = uiState.categories.toMutableMap().apply { put(name, "") },
                showAddCategoryDialog = false,
                newCategoryName = "",
            )
        } else {
            uiState = uiState.copy(showAddCategoryDialog = false, newCategoryName = "")
        }
    }

    fun saveMonthlyBudgetAndDistributeDefault() {
        val monthly = uiState.monthlyBudgetInput.toLongOrNull() ?: 0L
        var nextCategories = uiState.categories
        val names = nextCategories.keys.toList()

        if (monthly > 0 && names.isNotEmpty()) {
            val hasAnyInput = nextCategories.values.any { (it.toLongOrNull() ?: 0L) > 0L }
            if (!hasAnyInput) {
                val base = floor(monthly.toDouble() / names.size).toLong()
                var remainder = monthly - (base * names.size)
                nextCategories = nextCategories.toMutableMap().apply {
                    names.forEachIndexed { index, name ->
                        val extra = if (index == 0) remainder else 0L
                        if (index == 0) remainder = 0L
                        this[name] = (base + extra).toString()
                    }
                }
            }
        }

        uiState = uiState.copy(
            monthlyBudgetValue = monthly,
            categories = nextCategories,
            message = null,
        )
    }

    fun requestSaveBudget() {
        if (uiState.uid == null) {
            uiState = uiState.copy(message = "Please login to manage budgets.")
            return
        }

        if (isOverBudget) {
            return
        }

        if (remainingBudget > 0) {
            uiState = uiState.copy(showOtherConfirmDialog = true)
        } else {
            saveToFirebase(uiState.categories)
        }
    }

    fun closeOtherConfirmDialog() {
        uiState = uiState.copy(showOtherConfirmDialog = false)
    }

    fun confirmSaveAsOther() {
        val remain = remainingBudget
        val next = uiState.categories.toMutableMap()
        val currentOther = next["Other"]?.toLongOrNull() ?: 0L
        next["Other"] = (currentOther + remain).toString()
        uiState = uiState.copy(showOtherConfirmDialog = false, categories = next)
        saveToFirebase(next)
    }

    private fun saveToFirebase(currentCategories: Map<String, String>) {
        refreshMonthContext()
        val uid = uiState.uid ?: return

        val monthly = uiState.monthlyBudgetValue ?: (uiState.monthlyBudgetInput.toLongOrNull() ?: 0L)
        val payloadCategories = currentCategories.mapValues { (_, v) -> v.toLongOrNull() ?: 0L }

        uiState = uiState.copy(isSaving = true, message = null)

        val data = hashMapOf(
            "month" to uiState.monthKey,
            "monthlyBudget" to monthly,
            "categories" to payloadCategories,
        )

        firestore
            .collection("users")
            .document(uid)
            .collection("budget")
            .document(uiState.monthKey)
            .set(data)
            .addOnSuccessListener {
                uiState = uiState.copy(isSaving = false, message = "Budget saved.",)
            }
            .addOnFailureListener { e ->
                uiState = uiState.copy(isSaving = false, message = e.message ?: "Failed to save budget")
            }
    }
}

