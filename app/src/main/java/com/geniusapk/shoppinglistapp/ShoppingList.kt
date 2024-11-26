package com.geniusapk.shoppinglistapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ShoppingListScreen() {
    var shoppingItems by remember { mutableStateOf(emptyList<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(shoppingItems) { item ->
                    if (item.isEditing) {
                        EditableShoppingItem(
                            item = item,
                            onEditComplete = { name, quantity ->
                                shoppingItems = shoppingItems.map {
                                    if (it.id == item.id) it.copy(name = name, quantity = quantity, isEditing = false)
                                    else it.copy(isEditing = false)
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            onEditClick = {
                                shoppingItems = shoppingItems.map { it.copy(isEditing = it.id == item.id) }
                            },
                            onDeleteClick = {
                                shoppingItems = shoppingItems.filter { it.id != item.id }
                            }
                        )
                    }
                }
            }

            if (showDialog) {
                AddItemDialog(
                    itemName = newItemName,
                    itemQuantity = newItemQuantity,
                    onItemNameChange = { newItemName = it },
                    onItemQuantityChange = { newItemQuantity = it },
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        val quantity = newItemQuantity.toIntOrNull() ?: 1
                        shoppingItems = shoppingItems + ShoppingItem(
                            id = shoppingItems.size + 1,
                            name = newItemName,
                            quantity = quantity
                        )
                        newItemName = ""
                        newItemQuantity = "1"
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddItemDialog(
    itemName: String,
    itemQuantity: String,
    onItemNameChange: (String) -> Unit,
    onItemQuantityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Shopping Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = itemQuantity,
                    onValueChange = onItemQuantityChange,
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun EditableShoppingItem(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val quantity = editedQuantity.toIntOrNull() ?: 1
                    onEditComplete(editedName, quantity)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Update")
            }
        }
    }
}
