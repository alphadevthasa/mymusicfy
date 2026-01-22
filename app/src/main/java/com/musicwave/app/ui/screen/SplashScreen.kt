package com.musicwave.app.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    var animProgress by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (animProgress) 1f else 0.6f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (animProgress) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        animProgress = true
        delay(1800)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .drawWithContent {
                drawContent()
                // subtle radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x331DB954),
                            Color(0x001DB954)
                        ),
                        center = Offset(size.width / 2f, size.height * 0.4f)
                    ),
                    radius = size.width * 0.6f * glowAlpha
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Music note icon using simple geometric shapes
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1DB954),
                                Color(0xFF169C46)
                            )
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♪",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "MusicWave",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Putar musik tanpa batas",
                fontSize = 14.sp,
                color = Color(0xFFB3B3B3)
            )
        }

        // Loading dots at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) { i ->
                val dotAlpha by animateFloatAsState(
                    targetValue = if (animProgress) 1f else 0.3f,
                    animationSpec = tween(400 + i * 200),
                    label = "dot$i"
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dotAlpha)
                        .background(
                            color = Color(0xFF1DB954),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}
