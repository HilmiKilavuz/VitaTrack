package com.example.vitatrack.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vitatrack.domain.model.ChatMessage
import com.example.vitatrack.ui.theme.NavyDark
import com.example.vitatrack.ui.theme.Teal200
import com.example.vitatrack.ui.theme.Teal300
import com.example.vitatrack.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Yapay zeka sohbet ekranı.
 * Kullanıcı şikayetini yazar, Vita (Gemini AI) takviye önerir.
 *
 * Tasarım dili projenin mevcut Teal/Navy paleti ile uyumludur.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Yeni mesaj geldiğinde listenin en altına otomatik kaydır
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal200)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Header ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Teal200)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Geri butonu
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NavyDark
                    )
                }

                // Vita avatarı + başlık (ortada)
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dairesel avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(NavyDark, Color(0xFF1E3A5F))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌿", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vita",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "Sağlık Asistanı",
                            fontSize = 12.sp,
                            color = NavyDark.copy(alpha = 0.65f)
                        )
                    }
                }
            }

            // ─── Ana içerik: mesaj listesi + input ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Uyarı banner'ı — ekranın üstünde sabit
                    DisclaimerBanner()

                    // Mesaj listesi
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { message ->
                            MessageBubble(message = message)
                        }
                    }

                    // Mesaj giriş alanı
                    MessageInputRow(
                        text = inputText,
                        isLoading = isLoading,
                        onTextChange = viewModel::onInputChanged,
                        onSendClick = viewModel::sendMessage
                    )
                }
            }
        }
    }
}

// ─── Disclaimer Banner ───────────────────────────────────────────────────────

/**
 * Ekranın üstünde her zaman görünen uyarı şeridi.
 * Kullanıcıya önerilerin tavsiye niteliğinde olduğunu ve acil durumda
 * hastaneye başvurması gerektiğini hatırlatır.
 */
@Composable
private fun DisclaimerBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "⚠️", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Bu öneriler tavsiye niteliğindedir. Acil durumda 112'yi arayın veya en yakın hastaneye gidin.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onErrorContainer,
            lineHeight = 16.sp
        )
    }
}

// ─── Message Bubble ───────────────────────────────────────────────────────────

/**
 * Tek bir mesajı balonlarda gösterir.
 * Kullanıcı mesajları sağda (Teal), AI mesajları solda (gri kart) çizilir.
 */
@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // AI avatarı (sadece AI mesajlarında gösterilir)
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Teal200, Teal300)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌿", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Balon kutusu
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Teal200
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.isLoading) {
                // Yükleniyor animasyonu
                LoadingDots()
            } else {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (isUser) NavyDark else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ─── Loading Dots Animation ────────────────────────────────────────────────────

/**
 * AI yanıt üretirken gösterilen üç nokta animasyonu.
 * Her nokta sırayla yukarı-aşağı zıplar (InfiniteTransition).
 */
@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")

    @Composable
    fun animatedOffset(delayMs: Int): Float {
        val anim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -6f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(delayMs)
            ),
            label = "dot_$delayMs"
        )
        return anim
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        listOf(0, 150, 300).forEach { delay ->
            val offsetY = animatedOffset(delay)
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .offset(y = offsetY.dp)
                    .background(TextSecondary, CircleShape)
            )
        }
    }
}

// ─── Message Input Row ────────────────────────────────────────────────────────

/**
 * Ekranın altındaki mesaj yazma ve gönderme alanı.
 * İki parçadan oluşur: TextField ve Send IconButton.
 */
@Composable
private fun MessageInputRow(
    text: String,
    isLoading: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Belirtinizi veya sorunuzu yazın...",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal200,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Gönder butonu
            FilledIconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank() && !isLoading,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Teal200,
                    contentColor = NavyDark,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}
