package com.hendra.benerbenerrealalp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hendra.benerbenerrealalp.ui.viewmodel.FinanceViewModel
import com.hendra.benerbenerrealalp.ui.model.TransactionResponse
import com.hendra.benerbenerrealalp.ui.theme.BenerbenerrealALPTheme
import java.text.NumberFormat
import java.util.Locale

// --- PALET WARNA TEMA DARK ---
private val BgDark = Color(0xFF1F1F1F)
private val CardDark = Color(0xFF2C2C2E)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray = Color(0xFFAAAAAA)
private val GreenIncome = Color(0xFF66BB6A) // Hijau lebih soft
private val RedExpense = Color(0xFFEF5350)  // Merah soft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onBackClick: () -> Unit,
    viewModel: FinanceViewModel = viewModel(factory = FinanceViewModel.Factory)
) {
    val transactions by viewModel.transactions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Hitung Ringkasan Data
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    LaunchedEffect(true) { viewModel.loadData() }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = TextWhite, // Tombol putih agar kontras
                contentColor = BgDark
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Judul Besar
            Text(
                text = "Financial Flow",
                style = MaterialTheme.typography.headlineMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- KARTU UTAMA (SUMMARY CARD) ---
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Balance", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatRupiah(balance),
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Row Income & Expense
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Income Column
                        Column {
                            Text("Income", color = TextWhite, fontSize = 14.sp)
                            Text(
                                text = formatRupiah(totalIncome),
                                color = GreenIncome,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Expenses Column
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Expenses", color = TextWhite, fontSize = 14.sp)
                            Text(
                                text = formatRupiah(totalExpense),
                                color = RedExpense,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- DAFTAR TRANSAKSI ---
            Text(
                text = "Transaction",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { trx ->
                    TransactionItemCard(trx)
                }
            }
        }

        // --- DIALOG INPUT DATA ---
        if (showDialog) {
            AddTransactionDialog(
                onDismiss = { showDialog = false },
                onSave = { type, amount, category ->
                    viewModel.createTransaction(type, amount, category)
                    showDialog = false
                }
            )
        }
    }
}

// --- KOMPONEN ITEM TRANSAKSI (Sesuai Gambar) ---
@Composable
fun TransactionItemCard(trx: TransactionResponse) {
    val isIncome = trx.type == "INCOME"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ikon Bulat Putih dengan Panah
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isIncome) GreenIncome else RedExpense,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Teks Judul & Tanggal
                Column {
                    Text(
                        text = trx.category,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = trx.date.take(10), // Ambil tanggal saja
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Nominal Uang
            Text(
                text = "${if(isIncome) "+" else "-"} ${formatRupiah(trx.amount)}",
                color = if (isIncome) GreenIncome else RedExpense,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// --- KOMPONEN DIALOG INPUT ---
@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onSave: (String, Double, String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }

    AlertDialog(
        containerColor = CardDark,
        titleContentColor = TextWhite,
        textContentColor = TextGray,
        onDismissRequest = onDismiss,
        title = { Text("Tambah Transaksi") },
        text = {
            Column {
                // Pilihan Income / Expense
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = { type = "INCOME" },
                        label = { Text("Pemasukan") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenIncome,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = { type = "EXPENSE" },
                        label = { Text("Pengeluaran") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedExpense,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Nominal
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextGray,
                        focusedLabelColor = TextWhite,
                        unfocusedLabelColor = TextGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input Kategori
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (Contoh: Makan)") },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextGray,
                        focusedLabelColor = TextWhite,
                        unfocusedLabelColor = TextGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (amount.isNotEmpty() && category.isNotEmpty()) {
                        onSave(type, amount.toDouble(), category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TextWhite)
            ) {
                Text("Simpan", color = BgDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextGray) }
        }
    )
}

// Helper Format Rupiah
fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ")
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun FinanceScreenPreview() {
    BenerbenerrealALPTheme {
        // Dummy data preview
        val dummyData = listOf(
            TransactionResponse("1", "INCOME", 10000.0, "Jual Krupuk", "2025-11-30", "u1"),
            TransactionResponse("2", "EXPENSE", 10000.0, "Beli Diamond", "2025-11-30", "u1"),
            TransactionResponse("3", "INCOME", 10000.0, "Jual Krupuk", "2025-11-30", "u1")
        )
        // Kita tidak bisa inject viewModel di preview dengan mudah,
        // tapi kita bisa preview komponen itemnya:
        Column(modifier = Modifier.background(BgDark).padding(16.dp)) {
            TransactionItemCard(dummyData[0])
            Spacer(modifier = Modifier.height(8.dp))
            TransactionItemCard(dummyData[1])
        }
    }
}