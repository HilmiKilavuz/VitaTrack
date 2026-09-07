package com.example.vitatrack.ui.supplements.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vitatrack.ui.theme.NavyDark
import com.example.vitatrack.ui.theme.Teal200
import com.example.vitatrack.ui.theme.TextSecondary

/**
 * Yeni takviye ekleme veya mevcut takviyeyi düzenleme ekranı.
 *
 * @param supplementId Düzenlenecek takviyenin ID'si. null ise yeni ekleme modunda açılır.
 * @param onNavigateBack Geri tuşuna basılınca veya kayıt başarılı olunca çağrılır.
 *
 * Tasarım: Figma Finance App'ten ilham alınan Teal/Navy renk paleti.
 * - Koyu lacivert header
 * - Beyaz yüzer kart içerik alanı (yuvarlak üst köşeler)
 * - Teal renkli form elementleri
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal200) // Köşe boşluklarında teal rengi görünsün
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Teal yeşil header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Teal200)
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            ) {
                // Sol: Geri butonu
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NavyDark
                    )
                }
                // Orta: Ekran başlığı
                Text(
                    text = screenTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NavyDark,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // --- Beyaz içerik kartı (Figma'daki floating white card) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 100.dp)
                        .verticalScroll(rememberScrollState()), // Klavye açıldığında kaydırılabilir
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hata mesajı varsa göster
                    if (uiState.errorMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // --- Form başlığı bölümü ---
                    Text(
                        text = "Supplement Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // İsim alanı (teal focus rengi ile)
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Supplement Name") },
                        placeholder = { Text("e.g. Vitamin D") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal200,
                            focusedLabelColor = Teal200,
                            cursorColor = Teal200
                        )
                    )

                    // Doz alanı
                    OutlinedTextField(
                        value = uiState.dose,
                        onValueChange = { viewModel.onDoseChange(it) },
                        label = { Text("Dose") },
                        placeholder = { Text("e.g. 1000 mg") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal200,
                            focusedLabelColor = Teal200,
                            cursorColor = Teal200
                        )
                    )

                    // --- Hatırlatma bölümü ---
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reminder",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )

                    // Hatırlatma kartı
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Hatırlatma aç/kapa toggle'ı
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Dairesel ikon (Figma'daki zil ikonu gibi)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = Teal200.copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = Teal200,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Daily Reminder",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Get notified at a set time",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                Switch(
                                    checked = uiState.isReminderEnabled,
                                    onCheckedChange = { viewModel.onReminderEnabledChange(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Teal200
                                    )
                                )
                            }

                            // Hatırlatma açıksa saat seçici butonunu göster
                            if (uiState.isReminderEnabled) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                )
                                OutlinedButton(
                                    onClick = { showTimePicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Teal200
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, Teal200
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reminder Time: ${uiState.reminderTime}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // --- Kaydet FAB (sağ alt köşe) ---
                ExtendedFloatingActionButton(
                    onClick = { viewModel.saveSupplement() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    },
                    text = { Text("Save", fontWeight = FontWeight.SemiBold) },
                    containerColor = Teal200,
                    contentColor = NavyDark,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                )
            }
        }
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
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    selectorColor = Teal200,
                    timeSelectorSelectedContainerColor = Teal200.copy(alpha = 0.2f),
                    timeSelectorSelectedContentColor = Teal200
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Seçilen saati "HH:mm" formatına çeviriyoruz
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    onTimeSelected("$hour:$minute")
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Teal200)
            ) {
                Text("OK", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}
