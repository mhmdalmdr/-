package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SudXBronzeGold
import com.example.ui.theme.SudXChampagneGold
import com.example.ui.theme.SudXEmerald
import com.example.ui.theme.SudXGoldBorder
import com.example.ui.theme.SudXGoldTint
import com.example.ui.theme.SudXMidnightNavy
import com.example.ui.theme.SudXRoyalNavy
import com.example.ui.theme.SudXSapphireSlate
import com.example.ui.theme.SudXSoftGold

/**
 * Signature SudX gradient header bar with subtle metallic champagne gold bottom accent line.
 */
@Composable
fun SudXHeaderSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SudXMidnightNavy,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SudXMidnightNavy,
                            SudXRoyalNavy,
                            SudXSapphireSlate
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 300f)
                    )
                )
        ) {
            content()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SudXChampagneGold.copy(alpha = 0.75f),
                                SudXSoftGold.copy(alpha = 0.9f),
                                SudXChampagneGold.copy(alpha = 0.75f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Luxury interactive card with soft obsidian shadows, champagne hairline border,
 * and smooth hover & tap spring animations.
 */
@Composable
fun PremiumInteractiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    baseElevation: Dp = 5.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.97f
            isHovered -> 1.012f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "card_scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = when {
            isPressed -> 2.dp
            isHovered -> baseElevation + 4.dp
            else -> baseElevation
        },
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "card_elevation"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = when {
            isPressed -> SudXChampagneGold
            isHovered -> SudXChampagneGold.copy(alpha = 0.75f)
            else -> borderColor
        },
        animationSpec = tween(durationMillis = 180),
        label = "card_border"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                ambientColor = SudXMidnightNavy.copy(alpha = 0.10f),
                spotColor = SudXMidnightNavy.copy(alpha = 0.16f)
            )
            .clip(shape)
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = SudXChampagneGold),
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = shape,
        border = BorderStroke(1.dp, animatedBorderColor),
        content = content
    )
}

/**
 * Luxury container card with soft obsidian drop shadows and subtle champagne hairline border.
 */
@Composable
fun PremiumSurfaceCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = SudXMidnightNavy.copy(alpha = 0.08f),
                spotColor = SudXMidnightNavy.copy(alpha = 0.14f)
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        content = content
    )
}

/**
 * Interactive filled button with smooth spring tap & hover scale animation and soft shadow.
 */
@Composable
fun PremiumActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SudXRoyalNavy,
    contentColor: Color = SudXGoldTint,
    cornerRadius: Dp = 14.dp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btn_scale"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource = interactionSource)
            .shadow(
                elevation = if (isPressed) 2.dp else 5.dp,
                shape = shape,
                ambientColor = containerColor.copy(alpha = 0.25f),
                spotColor = containerColor.copy(alpha = 0.35f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * Interactive outlined button with smooth spring tap & hover scale animation.
 */
@Composable
fun PremiumOutlinedActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = SudXGoldBorder,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 14.dp,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "outlined_btn_scale"
    )

    val surfaceColor = MaterialTheme.colorScheme.surface
    val tintColor = MaterialTheme.colorScheme.secondaryContainer

    val animatedBg by animateColorAsState(
        targetValue = when {
            isPressed -> tintColor.copy(alpha = 0.9f)
            isHovered -> tintColor.copy(alpha = 0.5f)
            else -> surfaceColor
        },
        animationSpec = tween(150),
        label = "outlined_btn_bg"
    )

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .hoverable(interactionSource = interactionSource),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = animatedBg,
            contentColor = contentColor
        ),
        border = BorderStroke(1.2.dp, if (isPressed) SudXChampagneGold else borderColor),
        shape = RoundedCornerShape(cornerRadius),
        content = content
    )
}

/**
 * Standout Glassmorphic 'المعلم الذكي' (SudX AI Tutor) Hero Card with Obsidian-Gold luxury styling,
 * animated ambient glow, and lustrous CTA button.
 */
@Composable
fun GlassmorphicSmartTeacherCard(
    onNavigateToSmartTeacher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_teacher_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.38f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val outerShape = RoundedCornerShape(24.dp)
    val glassShape = RoundedCornerShape(18.dp)
    val ctaShape = RoundedCornerShape(16.dp)

    val btnInteractionSource = remember { MutableInteractionSource() }
    val isBtnPressed by btnInteractionSource.collectIsPressedAsState()
    val isBtnHovered by btnInteractionSource.collectIsHoveredAsState()
    val btnScale by animateFloatAsState(
        targetValue = when {
            isBtnPressed -> 0.96f
            isBtnHovered -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ai_cta_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SudXChampagneGold.copy(alpha = 0.28f * glowPulse),
                            SudXSapphireSlate.copy(alpha = 0.18f * glowPulse),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.55f),
                        radius = size.width * 0.74f
                    ),
                    cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                )
            }
            .shadow(
                elevation = 12.dp,
                shape = outerShape,
                ambientColor = SudXMidnightNavy.copy(alpha = 0.30f),
                spotColor = SudXChampagneGold.copy(alpha = 0.32f)
            )
            .testTag("prominent_ai_teacher_card"),
        shape = outerShape,
        colors = CardDefaults.cardColors(containerColor = SudXMidnightNavy),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    SudXChampagneGold.copy(alpha = 0.78f),
                    SudXSoftGold.copy(alpha = 0.32f),
                    SudXChampagneGold.copy(alpha = 0.65f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SudXMidnightNavy,
                            SudXRoyalNavy,
                            SudXSapphireSlate
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(950f, 650f)
                    )
                )
        ) {
            // Decorative luminous glass orbs in the background
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(x = (-30).dp, y = (-25).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SudXChampagneGold.copy(alpha = 0.24f * glowPulse),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 35.dp, y = 35.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SudXEmerald.copy(alpha = 0.16f * glowPulse),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Frosted Glassmorphism Inner Surface
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .clip(glassShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.11f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SudXSoftGold.copy(alpha = 0.42f),
                                Color.White.copy(alpha = 0.18f),
                                SudXChampagneGold.copy(alpha = 0.25f)
                            )
                        ),
                        shape = glassShape
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glowing Metallic Gold AI Badge
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .drawBehind {
                                drawCircle(
                                    color = SudXChampagneGold.copy(alpha = 0.34f * glowPulse),
                                    radius = size.minDimension * 0.68f
                                )
                            }
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SudXSoftGold,
                                        SudXChampagneGold,
                                        SudXBronzeGold
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = SudXGoldTint.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "المعلم الذكي",
                            tint = SudXMidnightNavy,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "المعلم الذكي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = SudXGoldTint
                            )
                            // Glassmorphic Online Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SudXEmerald.copy(alpha = 0.20f))
                                    .border(
                                        width = 1.dp,
                                        color = SudXEmerald.copy(alpha = 0.55f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4ADE80))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "متصل للتدريس",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFD1FAE5),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "مساعد SudX الشخصي لشرح المسائل والدروس خطوة بخطوة",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "«اسألني في أي مسألة أو علل أو قاعدة في الرياضيات، الفيزياء، الكيمياء، أو التاريخ وسأرشدك للحل بنفسك كأستاذك في الفصل!»",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.94f),
                    lineHeight = 23.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Glowing Champagne CTA Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = btnScale
                            scaleY = btnScale
                        }
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        SudXChampagneGold.copy(alpha = 0.42f * glowPulse),
                                        SudXSoftGold.copy(alpha = 0.55f * glowPulse),
                                        SudXChampagneGold.copy(alpha = 0.42f * glowPulse)
                                    )
                                ),
                                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                                size = size.copy(
                                    width = size.width + 6.dp.toPx(),
                                    height = size.height + 6.dp.toPx()
                                ),
                                topLeft = Offset((-3).dp.toPx(), (-3).dp.toPx())
                            )
                        }
                ) {
                    Button(
                        onClick = onNavigateToSmartTeacher,
                        interactionSource = btnInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .hoverable(interactionSource = btnInteractionSource)
                            .testTag("start_chat_with_tutor_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SudXGoldTint,
                            contentColor = SudXMidnightNavy
                        ),
                        shape = ctaShape,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 1.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SudXBronzeGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تحدث مع المعلم الذكي الآن",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = SudXMidnightNavy
                        )
                    }
                }
            }
        }
    }
}
