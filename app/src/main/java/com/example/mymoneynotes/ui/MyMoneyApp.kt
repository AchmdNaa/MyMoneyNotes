package com.example.mymoneynotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymoneynotes.Transaction
import com.example.mymoneynotes.ui.theme.*
import kotlinx.coroutines.launch
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoneyApp() {
    var transactions by remember { mutableStateOf(listOf<Transaction>()) }
    var isDarkMode by remember { mutableStateOf(false) }
    
    // Profile State
    var userName by remember { mutableStateOf("Achmad Naa") }
    var cardNumber by remember { mutableStateOf("1234 5678 1234 5678") }
    var cardExpiry by remember { mutableStateOf("12/26") }
    var cardHolder by remember { mutableStateOf("Achmad Naa") }
    var userLocation by remember { mutableStateOf("Surabaya, Jawa Timur, Indonesia") }

    MyMoneyNotesTheme(darkTheme = isDarkMode) {
        val localeID = Locale("id", "ID")
        val scope = rememberCoroutineScope()
        var showSheet by remember { mutableStateOf(false) }
        
        // Navigasi State
        var currentScreen by remember { mutableStateOf("home") } // "home", "report", "settings", "search"
        
        val sheetState = rememberModalBottomSheetState()
        
        val totalIncome by remember(transactions) {
            derivedStateOf { transactions.filter { it.type.equals("Income", ignoreCase = true) }.sumOf { it.amount } }
        }
        val totalExpense by remember(transactions) {
            derivedStateOf { transactions.filter { it.type.equals("Expense", ignoreCase = true) }.sumOf { it.amount } }
        }
        val balance by remember(totalIncome, totalExpense) {
            derivedStateOf { totalIncome - totalExpense }
        }

        Box {
            when (currentScreen) {
                "report" -> ReportScreen(transactions = transactions, onBack = { currentScreen = "home" })
                "search" -> SearchScreen(transactions = transactions, onBack = { currentScreen = "home" })
                "settings" -> SettingsScreen(
                    userName = userName,
                    onUserNameChange = { userName = it },
                    userLocation = userLocation,
                    onUserLocationChange = { userLocation = it },
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    cardExpiry = cardExpiry,
                    onCardExpiryChange = { cardExpiry = it },
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { isDarkMode = it },
                    onBack = { currentScreen = "home" }
                )
                else -> {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = { Text("Moneynotes", fontWeight = FontWeight.Bold, color = PurpleDark) },
                                navigationIcon = {
                                    IconButton(onClick = {}) {
                                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = PurpleDark, modifier = Modifier.size(20.dp))
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { currentScreen = "settings" }) {
                                        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = PurpleDark)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                            )
                        }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        ) {
                            // Profile Section
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = PurpleLight) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp), tint = PurplePrimary)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    Text(userLocation, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                            }

                            // Balance Card Circle Style
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                Surface(
                                    modifier = Modifier.size(200.dp),
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Brush.verticalGradient(colors = listOf(PurplePrimary, PurpleDark)), CircleShape)
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Total Saldo", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                                            Text(
                                                "Rp ${String.format(localeID, "%,.0f", balance)}", 
                                                style = MaterialTheme.typography.headlineSmall, 
                                                fontWeight = FontWeight.Black, 
                                                color = Color.White
                                            )
                                            Text("Dolor", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }

                            // Quick Actions
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                ActionButton(icon = Icons.Default.Add, label = "Tambah", onClick = { showSheet = true })
                                ActionButton(icon = Icons.Default.Search, label = "Cari", onClick = { currentScreen = "search" })
                                ActionButton(icon = Icons.Default.BarChart, label = "Laporan", onClick = { currentScreen = "report" })
                            }

                            // History Section (Glassmorphism look from image)
                            Surface(
                                modifier = Modifier.weight(1f).padding(top = 8.dp),
                                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                                color = PurpleLight.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(PurplePrimary.copy(alpha = 0.3f), CircleShape).align(Alignment.CenterHorizontally))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Riwayat Terakhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PurpleDark)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (transactions.isEmpty()) {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text("Belum ada transaksi", color = TextSecondary)
                                        }
                                    } else {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            contentPadding = PaddingValues(bottom = 24.dp)
                                        ) {
                                            items(transactions) { transaction -> 
                                                TransactionItem(transaction = transaction) 
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    AddTransactionForm(onAdd = { newTransaction ->
                        transactions = transactions + newTransaction
                        scope.launch { sheetState.hide() }.invokeOnCompletion { 
                            if (!sheetState.isVisible) showSheet = false 
                        }
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(transactions: List<Transaction>, onBack: () -> Unit) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val labelKey = remember { ExtraStore.Key<List<String>>() }
    val currentMonth = remember { SimpleDateFormat("MMM", Locale.getDefault()).format(Date()) }
    val localeID = Locale("id", "ID")

    LaunchedEffect(transactions) {
        val incomeData = mutableListOf<Float>()
        val expenseData = mutableListOf<Float>()
        val sortedTransactions = transactions.reversed()
        
        sortedTransactions.forEach { transaction ->
            if (transaction.type.equals("Income", ignoreCase = true)) {
                incomeData.add(transaction.amount.toFloat())
                expenseData.add(0f)
            } else {
                incomeData.add(0f)
                expenseData.add(transaction.amount.toFloat())
            }
        }
        
        if (incomeData.isEmpty()) incomeData.add(0f)
        if (expenseData.isEmpty()) expenseData.add(0f)
        
        modelProducer.runTransaction {
            columnSeries {
                series(incomeData)
                series(expenseData)
            }
            updateExtras { it[labelKey] = List(maxOf(incomeData.size, expenseData.size, 1)) { currentMonth } }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Laporan Keuangan", fontWeight = FontWeight.Bold, color = PurpleDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            // Chart Card (Style dari gambar)
            Surface(
                modifier = Modifier.fillMaxWidth().height(350.dp),
                shape = RoundedCornerShape(32.dp),
                color = PurplePrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = PurpleDark, fontWeight = FontWeight.Medium)
                        val totalIncome = transactions.filter { it.type.equals("Income", ignoreCase = true) }.sumOf { it.amount }
                        val totalExpense = transactions.filter { it.type.equals("Expense", ignoreCase = true) }.sumOf { it.amount }
                        val reportTotal = totalIncome - totalExpense
                        Text("Rp ${String.format(localeID, "%,.0f", reportTotal)}", color = PurpleDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = IncomeGreen, label = "Pemasukan")
                        Spacer(modifier = Modifier.width(24.dp))
                        LegendItem(color = ExpenseRed, label = "Pengeluaran")
                    }
                    
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberColumnCartesianLayer(
                                columns = listOf(
                                    rememberLineComponent(color = IncomeGreen, thickness = 12.dp, shape = com.patrykandpatrick.vico.core.common.shape.Shape.rounded(4.dp)),
                                    rememberLineComponent(color = ExpenseRed, thickness = 12.dp, shape = com.patrykandpatrick.vico.core.common.shape.Shape.rounded(4.dp))
                                ),
                                mergeMode = ColumnCartesianLayer.MergeMode.Grouped
                            ),
                            startAxis = null,
                            bottomAxis = rememberBottomAxis(
                                label = rememberAxisLabelComponent(),
                                valueFormatter = { value, chartValues, _ ->
                                    chartValues.model.extraStore[labelKey].getOrNull(value.toInt()) ?: ""
                                }
                            ),
                        ),
                        modelProducer = modelProducer,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Info List di bawah grafik (Style dari gambar)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = PurplePrimary.copy(alpha = 0.2f)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(PurplePrimary.copy(alpha = 0.3f), CircleShape).align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada transaksi", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(transactions) { transaction ->
                                val amountSign = if (transaction.type.equals("Income", ignoreCase = true)) "+" else "-"
                                TransactionItemGlass(
                                    transaction.title,
                                    "Hari ini", // Bisa disesuaikan jika ada field tanggal
                                    "$amountSign Rp ${String.format(localeID, "%,.0f", transaction.amount)}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemGlass(title: String, date: String, amount: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(date, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Text(amount, fontWeight = FontWeight.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userName: String,
    onUserNameChange: (String) -> Unit,
    userLocation: String,
    onUserLocationChange: (String) -> Unit,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var notification by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {
            // Card Settings Style (Kanan gambar)
            Surface(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(32.dp),
                color = PurplePrimary.copy(alpha = 0.4f)
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Column {
                        Text(cardNumber, color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Text("Card number", color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.weight(1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Name", color = Color.White.copy(alpha = 0.7f))
                                Text(userName, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(cardExpiry, color = Color.White)
                                Text("VALID THRU", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Settings Options
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = PurplePrimary.copy(alpha = 0.2f)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(PurplePrimary.copy(alpha = 0.3f), CircleShape).align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Edit Profil", fontWeight = FontWeight.Bold, color = PurpleDark)
                    TextField(value = userName, onValueChange = onUserNameChange, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = userLocation, onValueChange = onUserLocationChange, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = cardNumber, onValueChange = onCardNumberChange, label = { Text("Nomor Kartu") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(value = cardExpiry, onValueChange = onCardExpiryChange, label = { Text("Masa Berlaku (MM/YY)") }, modifier = Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    SettingToggle("Mode Gelap", "Ubah tema aplikasi", isDarkMode) { onDarkModeChange(it) }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.3f))
                    SettingToggle("Notifikasi", "Dapatkan update transaksi", notification) { notification = it }
                }
            }
        }
    }
}

@Composable
fun SettingToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = PurpleDark)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurplePrimary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(transactions: List<Transaction>, onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTransactions by remember(searchQuery, transactions) {
        derivedStateOf {
            if (searchQuery.isEmpty()) emptyList()
            else transactions.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari transaksi...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = PurpleDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 24.dp)) {
            if (filteredTransactions.isEmpty() && searchQuery.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Transaksi tidak ditemukan", color = TextSecondary) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
                    items(filteredTransactions) { transaction -> TransactionItem(transaction = transaction) }
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = PurpleLight, modifier = Modifier.size(60.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label, tint = PurplePrimary) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
