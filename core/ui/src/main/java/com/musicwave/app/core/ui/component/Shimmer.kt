package com.musicwave.app.core.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

private val shimmerColors = listOf(
    Color(0xFF282828),
    Color(0xFF3A3A3A),
    Color(0xFF282828)
)

@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateX, 0f),
        end = Offset(translateX + 300f, 0f)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun ShimmerCircle(size: Int = 48) {
    ShimmerBlock(
        modifier = Modifier.size(size.dp),
        shape = CircleShape
    )
}

@Composable
fun ShimmerRect(width: Int = 150, height: Int = 150, shape: Shape = RoundedCornerShape(4.dp)) {
    ShimmerBlock(
        modifier = Modifier.size(width.dp, height.dp),
        shape = shape
    )
}

@Composable
fun ShimmerLine(width: Int = 120, height: Int = 14) {
    ShimmerBlock(
        modifier = Modifier.size(width.dp, height.dp),
        shape = RoundedCornerShape(4.dp)
    )
}
