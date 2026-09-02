package com.example.vitatrack.ui.supplements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vitatrack.domain.model.Supplement

/**
 * Uygulamanın ana ekranıdır.
 * Kayıtlı tüm takviyeleri liste halinde gösterir.
 *
 * @Composable: Bu fonksiyon Jetpack Compose ile çizilen bir UI bileşenidir.
 * viewModel: Hilt tarafından otomatik oluşturulup sağlanır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementListScreen(
    viewModel: SupplementListViewModel = hiltViewModel(),
    onAddClick: () -> Unit = {}
) {
    // viewModel'deki StateFlow'u Compose'un anlayacağı State'e çeviriyoruz.
    // Liste her değiştiğinde Compose ekranı otomatik yeniden çizer.
    val supplements by viewModel.supplements.collectAsState()

    Scaffold(
        // Üst bar
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "VitaTrack 💊",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        // Sağ alt köşedeki yuvarlak "+" butonu (FAB = Floating Action Button)
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Supplement",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        if (supplements.isEmpty()) {
            // Liste boşsa boş durum ekranı göster
            EmptyStateContent(modifier = Modifier.padding(innerPadding))
        } else {
            // Liste doluysa takviyeleri listele
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = supplements,
                    // Her öğenin key'i benzersiz olmalı, böylece Compose animasyonlar için öğeyi takip eder
                    key = { it.id }
                ) { supplement ->
                    SupplementItem(
                        supplement = supplement,
                        onDeleteClick = { viewModel.deleteSupplement(supplement) }
                    )
                }
            }
        }
    }
}

/**
 * Her bir takviyeyi gösteren kart bileşeni.
 */
@Composable
fun SupplementItem(
    supplement: Supplement,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf: Renkli ilaç ikonu
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💊", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Orta: Takviye adı ve detayları
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = supplement.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = supplement.dose,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Hatırlatma durumu ikonu
                    Icon(
                        imageVector = if (supplement.isReminderEnabled)
                            Icons.Default.Notifications
                        else
                            Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (supplement.isReminderEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (supplement.isReminderEnabled)
                            supplement.reminderTime
                        else
                            "No reminder",
                        fontSize = 12.sp,
                        color = if (supplement.isReminderEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sağ: Sil butonu
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Liste boş olduğunda kullanıcıya yönlendirici bir mesaj gösterir.
 */
@Composable
fun EmptyStateContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💊", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No supplements yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first supplement",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
