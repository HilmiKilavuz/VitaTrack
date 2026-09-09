package com.example.vitatrack.ui.supplements.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.vitatrack.ui.theme.NavyDark
import com.example.vitatrack.ui.theme.Teal200
import com.example.vitatrack.ui.theme.TextSecondary

/**
 * Supplement seçme dialog'u.
 *
 * - Üstte arama kutusu: Listeyi anlık filtreler
 * - Ortada LazyColumn: Alfabetik sıralı supplement listesi
 * - En altta sabit "Other" satırı: Tıklanınca elle giriş alanı açılır
 * - Seçilen satır teal check işareti ile işaretlenir
 *
 * @param currentName    Şu an seçili olan (forma yüklü) isim; listedeki ilgili satırı işaretler
 * @param onSupplementSelected  Kullanıcı bir seçim yapıp "OK" bastığında ismi geri döndürür
 * @param onDismiss      Dialog kapatıldığında çağrılır
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementPickerDialog(
    currentName: String = "",
    onSupplementSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Arama kutusu metni
    var searchQuery by remember { mutableStateOf("") }

    // Şu an seçili öğe (dialog içinde geçici seçim)
    var selectedItem by remember {
        mutableStateOf(
            if (currentName.isNotBlank()) currentName else ""
        )
    }

    // "Other" seçildiğinde elle giriş modu aktif olur
    var isOtherMode by remember {
        mutableStateOf(
            currentName.isNotBlank() &&
                    !SupplementPickerData.supplements.dropLast(1).contains(currentName)
        )
    }
    var customName by remember {
        mutableStateOf(if (isOtherMode) currentName else "")
    }

    val customNameFocusRequester = remember { FocusRequester() }

    // "Other" seçilince klavyeyi aç
    LaunchedEffect(isOtherMode) {
        if (isOtherMode) {
            try { customNameFocusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    // Arama filtrelenmiş liste — "Other" her zaman görünür (arama ona dokunmaz)
    val predefinedList = SupplementPickerData.supplements.dropLast(1) // "Other" hariç
    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) predefinedList
        else predefinedList.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Başlık ──────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Teal200)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "Select Supplement",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NavyDark
                    )
                }

                // ── Arama kutusu ────────────────────────────────────────
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    placeholder = { Text("Search supplements...", color = TextSecondary) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal200,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        cursorColor = Teal200
                    )
                )

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // ── Supplement listesi ──────────────────────────────────
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList, key = { it }) { supplement ->
                        val isSelected = selectedItem == supplement && !isOtherMode
                        PickerRow(
                            label = supplement,
                            isSelected = isSelected,
                            isOtherRow = false,
                            onClick = {
                                selectedItem = supplement
                                isOtherMode = false
                                searchQuery = ""
                            }
                        )
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // ── "Other" satırı — her zaman en altta sabit ──────────
                PickerRow(
                    label = SupplementPickerData.OTHER_OPTION,
                    isSelected = isOtherMode,
                    isOtherRow = true,
                    onClick = {
                        isOtherMode = true
                        selectedItem = SupplementPickerData.OTHER_OPTION
                    }
                )

                // "Other" seçiliyse elle giriş alanını göster
                if (isOtherMode) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .focusRequester(customNameFocusRequester),
                        label = { Text("Supplement name") },
                        placeholder = { Text("Type supplement name...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (customName.isNotBlank()) {
                                    onSupplementSelected(customName.trim())
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal200,
                            focusedLabelColor = Teal200,
                            cursorColor = Teal200
                        )
                    )
                }

                // ── Alt butonlar ────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val result = when {
                                isOtherMode && customName.isNotBlank() -> customName.trim()
                                !isOtherMode && selectedItem.isNotBlank() -> selectedItem
                                else -> return@Button
                            }
                            onSupplementSelected(result)
                        },
                        enabled = if (isOtherMode) customName.isNotBlank() else selectedItem.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Teal200,
                            contentColor = NavyDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("OK", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Yardımcı: Tek bir satır bileşeni ──────────────────────────────────────────

@Composable
private fun PickerRow(
    label: String,
    isSelected: Boolean,
    isOtherRow: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Teal200.copy(alpha = 0.10f) else Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Other" satırında kalem ikonu
        if (isOtherRow) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = if (isSelected) Teal200 else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                isSelected && isOtherRow -> Teal200
                isSelected -> MaterialTheme.colorScheme.onSurface
                isOtherRow -> TextSecondary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        // Seçili satırda teal check ikonu
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Teal200,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}
