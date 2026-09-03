package com.example.vitatrack.ui.supplements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Yeni takviye ekleme veya mevcut takviyeyi düzenleme ekranı.
 *
 * @param supplementId Düzenlenecek takviyenin ID'si. null ise yeni ekleme modunda açılır.
 * @param onNavigateBack Geri tuşuna basılınca veya kayıt başarılı olunca çağrılır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSupplementScreen(
    supplementId: Int? = null,
    onNavigateBack: () -> Unit = {},
    viewModel: AddEditSupplementViewModel = hiltViewModel()
) {
    // Düzenleme modundaysa mevcut veriyi forma yükle (sadece bir kez çalışır)
    LaunchedEffect(supplementId) {
        if (supplementId != null) {
            viewModel.loadSupplement(supplementId)
        }
    }

    // UiState'i dinleyip değerleri alıyoruz
    val uiState by viewModel.uiState.collectAsState()

    // Kayıt başarılıysa otomatik geri git
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    // Saat seçici dialog'unun açık/kapalı durumu
    var showTimePicker by remember { mutableStateOf(false) }

    // Başlıkta "Add" ya da "Edit" yazısı (moda göre)
    val screenTitle = if (supplementId == null) "Add Supplement" else "Edit Supplement"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Geri butonu
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // Kaydet butonu (sağ alt köşe)
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveSupplement() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save"
                    )
                },
                text = { Text("Save") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Klavye açıldığında kaydırılabilir
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hata mesajı varsa göster
            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // İsim alanı
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Supplement Name") },
                placeholder = { Text("e.g. Vitamin D") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                )
            )

            // Doz alanı
            OutlinedTextField(
                value = uiState.dose,
                onValueChange = { viewModel.onDoseChange(it) },
                label = { Text("Dose") },
                placeholder = { Text("e.g. 1000 mg") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Hatırlatma bölümü
            HorizontalDivider()

            Text(
                text = "Reminder",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Hatırlatma aç/kapa toggle'ı
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Enable daily reminder",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = uiState.isReminderEnabled,
                    onCheckedChange = { viewModel.onReminderEnabledChange(it) }
                )
            }

            // Hatırlatma açıksa saat seçici butonunu göster
            if (uiState.isReminderEnabled) {
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⏰  Reminder Time: ${uiState.reminderTime}",
                        fontSize = 16.sp
                    )
                }
            }

            // Alt boşluk (FAB'ın üstünü kapatmaması için)
            Spacer(modifier = Modifier.height(72.dp))
        }

        // Saat seçici dialog
        if (showTimePicker) {
            TimePickerDialog(
                initialTime = uiState.reminderTime,
                onTimeSelected = { selectedTime ->
                    viewModel.onReminderTimeChange(selectedTime)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }
    }
}

/**
 * Saat seçici dialog bileşeni.
 * Material 3'ün TimePicker bileşenini kullanır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // initialTime ("HH:mm") parse ediyoruz
    val parts = initialTime.split(":")
    val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Reminder Time") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                // Seçilen saati "HH:mm" formatına çeviriyoruz
                val hour = timePickerState.hour.toString().padStart(2, '0')
                val minute = timePickerState.minute.toString().padStart(2, '0')
                onTimeSelected("$hour:$minute")
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
