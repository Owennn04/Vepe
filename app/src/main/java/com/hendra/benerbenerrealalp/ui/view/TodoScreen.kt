package com.hendra.benerbenerrealalp.ui.view

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.benerbenerrealalp.ui.model.TodoResponse
import com.hendra.benerbenerrealalp.ui.theme.BenerbenerrealALPTheme
import com.hendra.benerbenerrealalp.ui.viewmodel.TodoViewModel
import java.util.Calendar

// --- PALET WARNA DARK MODE ---
private val Dark = Color(0xFF121212)
private val CDark = Color(0xFF1E1E1E) // Warna kartu input & list
private val InputDark = Color(0xFF2C2C2E) // Warna kolom input
private val TWhites = Color(0xFFFFFFFF)
private val TGray = Color(0xFF888888)
private val AccentBlue = Color(0xFF5C6BC0) // Warna icon jam
private val CheckboxColor = Color(0xFF7C4DFF)

@Composable
fun TodoScreen(
    onBackClick: () -> Unit,
    viewModel: TodoViewModel = viewModel(factory = TodoViewModel.Factory)
) {
    val todos by viewModel.todos.collectAsState()
    val context = LocalContext.current

    // Load data awal
    LaunchedEffect(true) { viewModel.loadTodos() }

    TodoScreenContent(
        todos = todos,
        onBackClick = onBackClick,
        onAddTodo = { title, time ->
            viewModel.addTodo(context, title, time)
        },
        onToggleTodo = { id -> viewModel.toggleTodo(id) },
        onDeleteTodo = { id -> viewModel.deleteTodo(id) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreenContent(
    todos: List<TodoResponse>,
    onBackClick: () -> Unit,
    onAddTodo: (String, String) -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Time Picker Logic
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            time = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        containerColor = Dark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("To Do List", color = TWhites, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TWhites)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Dark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Header Besar


            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. INPUT CARD (Gaya Modern) ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CDark),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tambah To Do List", color = TGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Judul
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Judul", color = TGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = InputDark,
                            unfocusedContainerColor = InputDark,
                            focusedTextColor = TWhites,
                            unfocusedTextColor = TWhites,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Input Jam (Clickable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(InputDark)
                                .clickable { timePickerDialog.show() }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, null, tint = TWhites, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(time, color = TWhites, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Tombol Add
                        Button(
                            onClick = {
                                if (title.isNotEmpty()) {
                                    onAddTodo(title, time)
                                    title = ""
                                }
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                        ) {
                            Text("+ ADD", color = TWhites, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. DAFTAR TUGAS ---
            Text(
                text = "Daftar Tugas",
                style = MaterialTheme.typography.titleMedium,
                color = TWhites,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(todos) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggle = { onToggleTodo(todo.id) },
                        onDelete = { onDeleteTodo(todo.id) }
                    )
                }
            }

            // Ikon Sampah di bawah (Visual Only / Clear All)
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFEF5350))
                Text("Delete", color = Color(0xFFEF5350), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TodoItemCard(
    todo: TodoResponse,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Custom
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = CheckboxColor,
                    uncheckedColor = TGray,
                    checkmarkColor = TWhites
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (todo.isCompleted) TGray else TWhites,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = todo.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = TWhites
                    )
                }
            }

            // Delete Icon
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF5350) // Merah
                )
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    BenerbenerrealALPTheme {
        // Dummy data untuk preview
        val dummyTodos = listOf(
            TodoResponse("1", "Tidur", "08:10", true, false),
            TodoResponse("2", "Nugas", "08:10", true, true),
            TodoResponse("3", "Olahraga", "08:10", true, false),
        )

        TodoScreenContent(
            todos = dummyTodos,
            onBackClick = {},
            onAddTodo = { _, _ -> },
            onToggleTodo = {},
            onDeleteTodo = {}
        )
    }
}