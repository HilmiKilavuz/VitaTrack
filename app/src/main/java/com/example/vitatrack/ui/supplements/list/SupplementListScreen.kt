package com.example.vitatrack.ui.supplements.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vitatrack.domain.model.Supplement
import com.example.vitatrack.ui.theme.NavyDark
import com.example.vitatrack.ui.theme.Teal200
import com.example.vitatrack.ui.theme.TextSecondary

/**
 * Uygulamanın ana ekranıdır.
 * Kayıtlı tüm takviyeleri liste halinde gösterir.
 *
 * @Composable: Bu fonksiyon Jetpack Compose ile çizilen bir UI bileşenidir.
 * viewModel: Hilt tarafından otomatik oluşturulup sağlanır.
 *
 * Tasarım: Figma Finance App'ten ilham alınan Teal/Navy renk paleti.
 * - Koyu lacivert header
 * - Beyaz yüzer kart alanı (yuvarlak üst köşeler)
 * - Dairesel teal ikon container'lar
 * - Teal renkli hatırlatma saati metni
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementListScreen(
    viewModel: SupplementListViewModel = hiltViewModel(),
    onAddClick: () -> Unit = {},
    onEditClick: (Int) -> Unit = {}
) {
    // viewModel'deki StateFlow'u Compose'un anlayacağı State'e çeviriyoruz.
    // Liste her değiştiğinde Compose ekranı otomatik yeniden çizer.
    val supplements by viewModel.supplements.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal200) // Köşe boşluklarında teal rengi görünsün (beyaz kartın yuvarlak köşeleri için)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Koyu lacivert header (Figma'daki Notification ekranı header'ı gibi) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Teal200)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Orta: Uygulama başlığı
                Text(
                    text = "VitaTrack 💊",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = NavyDark,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            // --- Beyaz içerik kartı (Figma'daki floating white card) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (supplements.isEmpty()) {
                    // Liste boşsa boş durum ekranı göster
                    EmptyStateContent(modifier = Modifier.fillMaxSize())
                } else {
                    // Liste doluysa takviyeleri listele
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 100.dp, // FAB'ın üstüne taşmaması için
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Bölüm başlığı
                        item {
                            Text(
                                text = "My Supplements",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                modifier = Modifier.padding(
                                    start = 4.dp,
                                    bottom = 12.dp,
                                    top = 4.dp
                                )
                            )
                        }
                        items(
                            items = supplements,
                            // Her öğenin key'i benzersiz olmalı, böylece Compose animasyonlar için öğeyi takip eder
                            key = { it.id }
                        ) { supplement ->
                            SupplementItem(
                                supplement = supplement,
                                onEditClick = { onEditClick(supplement.id) },
                                onDeleteClick = { viewModel.deleteSupplement(supplement) }
                            )
                            // Satırlar arası ince divider (Figma'daki gibi)
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 68.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            )
                        }
                    }
                }

                // --- FAB: Sağ alt köşe ---
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = Teal200,
                    contentColor = NavyDark,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Supplement"
                    )
                }
            }
        }
    }
}

/**
 * Her bir takviyeyi gösteren satır bileşeni.
 * Figma'daki bildirim satırı stiline benzer:
 * - Sol: Dairesel teal ikon container
 * - Orta: İsim + doz
 * - Sağ alt: Teal renkli hatırlatma saati
 */
@Composable
fun SupplementItem(
    supplement: Supplement,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sol: Dairesel teal ikon container (Figma'daki gibi)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Teal200, Color(0xFF00B386))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💊", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Orta: Takviye adı ve detayları
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = supplement.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = supplement.dose,
                fontSize = 13.sp,
                color = TextSecondary
            )
            // Hatırlatma saati (Figma'daki teal timestamp gibi)
            if (supplement.isReminderEnabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Teal200,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = supplement.reminderTime,
                        fontSize = 12.sp,
                        color = Teal200,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "No reminder",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
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

/**
 * Liste boş olduğunda kullanıcıya yönlendirici bir mesaj gösterir.
 */
@Composable
fun EmptyStateContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Büyük dairesel ikon (boş durum için)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Teal200, Color(0xFF00B386))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💊", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
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
                color = TextSecondary
            )
        }
    }
}
