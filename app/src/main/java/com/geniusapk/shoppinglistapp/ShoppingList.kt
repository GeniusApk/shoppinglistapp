package com.geniusapk.shoppinglistapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//if you want to use any kind of data then you have to make data class

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false
)

@Preview
@Composable
fun MainUi() {
    var shoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(               //adding button
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp)
        ) {
            Text(text = "Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shoppingItems) { item ->
                if (item.isEditing) {            //if item is editing then it will show
                    ShoppingItemEditing(item = item, onEditComplete = { editedItem, editedQuantity ->
                        shoppingItems = shoppingItems.map { it.copy(isEditing = false) }
                        val editedShoppingItem = shoppingItems.find { it.id == item.id }
                        editedShoppingItem?.let {
                            it.name = editedItem
                            it.quantity = editedQuantity
                        }
                    })
                } else {
                    ShoppingListItem(item = item, onEditClick = {
                        //finding out which item we are editing
                        shoppingItems = shoppingItems.map {
                            it.copy(isEditing = it.id == item.id)
                        }
                    }, onDeleteClick = {
                        shoppingItems = shoppingItems - item
                    })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }

                    Button(onClick = {
                        if (itemName.isNotBlank()) {
                            val quantity = itemQuantity.toIntOrNull() ?: 0 // Parse quantity safely
                            val newItem = ShoppingItem(
                                id = shoppingItems.size + 1,
                                name = itemName,
                                quantity = quantity
                            )
                            shoppingItems = shoppingItems + newItem
                            showDialog = false
                            itemName = ""
                            itemQuantity = "0" // Reset quantity field after adding item
                        }
                    }) {
                        Text(text = "Add")
                    }

                }
            },
            title = { Text(text = "Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Enter Item") }
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(text = "Enter Quantity") }
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.padding(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.name, modifier = Modifier.padding(8.dp))
            Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
            Row(modifier = Modifier) {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ShoppingItemEditing(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editedItem by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Card(modifier = Modifier.padding(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column {
                BasicTextField(
                    value = editedItem,
                    onValueChange = { editedItem = it },
                    singleLine = true,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp)
                )
                BasicTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    singleLine = true,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp)
                )
            }
            Button(
                onClick = {
                    val quantity = editedQuantity.toIntOrNull() ?: 1
                    val editedShoppingItem = item.copy(name = editedItem, quantity = quantity)
                    onEditComplete(editedItem, quantity)
                }
            ) {
                Text(text = "Update")
            }
        }
    }

}