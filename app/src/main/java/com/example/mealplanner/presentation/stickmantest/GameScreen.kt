package com.example.mealplanner.presentation.stickmantest

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen() {
    val scope = rememberCoroutineScope()
    val startX = 100f
    val endX = 900f // beam đến enemy
    val attackX = remember { Animatable(startX) }
    var showBeam by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
            // Draw player
            drawCircle(Color.Black, radius = 40f, center = Offset(100f, 100f))
            drawLine(Color.Black, Offset(100f, 140f), Offset(100f, 220f), strokeWidth = 6f)

            // Draw enemy
            drawCircle(Color.Red, radius = 40f, center = Offset(size.width - 100f, 100f))
            drawLine(Color.Red, Offset(size.width - 100f, 140f), Offset(size.width - 100f, 220f), strokeWidth = 6f)

            // Beam attack line
            if (showBeam) {
                drawLine(
                    color = Color.Yellow,
                    start = Offset(startX, 100f),
                    end = Offset(attackX.value, 100f),
                    strokeWidth = 6f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                showBeam = true
                attackX.snapTo(startX)
                attackX.animateTo(endX, animationSpec = tween(300))
                delay(200)
                showBeam = false
            }
        }) {
            Text("Simulate Attack")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StickmanCanvas( attackX: Float?) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Player Stickman
        drawCircle(Color.Black, radius = 40f, center = Offset(100f, 100f)) // Head
        drawLine(Color.Black, Offset(100f, 140f), Offset(100f, 220f), strokeWidth = 6f) // Body
        drawLine(Color.Black, Offset(100f, 160f), Offset(70f, 190f), strokeWidth = 6f) // Left arm
        drawLine(Color.Black, Offset(100f, 160f), Offset(130f, 190f), strokeWidth = 6f) // Right arm
        drawLine(Color.Black, Offset(100f, 220f), Offset(80f, 270f), strokeWidth = 6f) // Left leg
        drawLine(Color.Black, Offset(100f, 220f), Offset(120f, 270f), strokeWidth = 6f) // Right leg

        // Enemy Stickman
        val enemyX = size.width - 100f
        drawCircle(Color.Red, radius = 40f, center = Offset(enemyX, 100f))
        drawLine(Color.Red, Offset(enemyX, 140f), Offset(enemyX, 220f), strokeWidth = 6f)
        drawLine(Color.Red, Offset(enemyX, 160f), Offset(enemyX - 30f, 190f), strokeWidth = 6f)
        drawLine(Color.Red, Offset(enemyX, 160f), Offset(enemyX + 30f, 190f), strokeWidth = 6f)
        drawLine(Color.Red, Offset(enemyX, 220f), Offset(enemyX - 20f, 270f), strokeWidth = 6f)
        drawLine(Color.Red, Offset(enemyX, 220f), Offset(enemyX + 20f, 270f), strokeWidth = 6f)

        // Draw beam if animating
        attackX?.let {
            drawLine(
                color = Color.Yellow,
                start = Offset(100f, 100f),
                end = Offset(it, 100f),
                strokeWidth = 6f
            )
        }
    }
}

@Composable
fun StickmanWithSwordCanvas() {
    val armAngle = remember { Animatable(0f) } // Góc cánh tay
    val scope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Canvas vẽ stickman
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
            val centerX = 100f
            val centerY = 100f

            // Vẽ đầu & thân
            drawCircle(Color.Black, radius = 40f, center = Offset(centerX, centerY))
            drawLine(Color.Black, Offset(centerX, centerY + 40f), Offset(centerX, centerY + 120f), strokeWidth = 6f)

            // Vẽ chân
            drawLine(Color.Black, Offset(centerX, centerY + 120f), Offset(centerX - 30f, centerY + 180f), strokeWidth = 6f)
            drawLine(Color.Black, Offset(centerX, centerY + 120f), Offset(centerX + 30f, centerY + 180f), strokeWidth = 6f)

            // Vẽ tay trái
            drawLine(Color.Black, Offset(centerX, centerY + 60f), Offset(centerX - 40f, centerY + 90f), strokeWidth = 6f)

            // Vẽ tay phải + kiếm
            val armLength = 60f
            val angleRad = Math.toRadians(armAngle.value.toDouble())
            val armEndX = centerX + (armLength * cos(angleRad)).toFloat()
            val armEndY = centerY + 60f - (armLength * sin(angleRad)).toFloat()

            drawLine(Color.Black, Offset(centerX, centerY + 60f), Offset(armEndX, armEndY), strokeWidth = 6f)

            // Vẽ kiếm từ bàn tay
            val swordLength = 50f
            val swordEndX = armEndX + (swordLength * cos(angleRad)).toFloat()
            val swordEndY = armEndY - (swordLength * sin(angleRad)).toFloat()
            drawLine(Color.Gray, Offset(armEndX, armEndY), Offset(swordEndX, swordEndY), strokeWidth = 8f)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút vung kiếm
        Button(onClick = {
            scope.launch {
                armAngle.snapTo(0f)
                armAngle.animateTo(
                    targetValue = 60f,
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                )
                delay(200)
                armAngle.animateTo(0f, animationSpec = tween(300))
            }
        }) {
            Text("Vung kiếm ⚔️")
        }
    }
}

@Composable
fun StickmanFightScene() {
    var triggerFight by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val rightArmAngle = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Vị trí stickman A
            val centerA = Offset(200f, size.height / 2)

            // Người & đầu
            drawCircle(Color.Black, 40f, center = centerA)
            drawLine(Color.Black, centerA.copy(y = centerA.y + 40f), centerA.copy(y = centerA.y + 140f), strokeWidth = 6f)

            // Tay phải có animation
            val handLength = 60f
            val angleRad = Math.toRadians(rightArmAngle.value.toDouble()).toFloat()
            val handEnd = Offset(
                x = centerA.x + handLength * cos(angleRad),
                y = centerA.y + 60f + handLength * sin(angleRad)
            )
            drawLine(Color.Black, centerA.copy(y = centerA.y + 60f), handEnd, strokeWidth = 6f)

            // Tay trái cố định
            drawLine(Color.Black, centerA.copy(y = centerA.y + 60f), Offset(centerA.x - 60f, centerA.y + 100f), strokeWidth = 6f)
        }

        // Nút bấm khởi động hoạt ảnh
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = {
                triggerFight = true
                scope.launch {
                    rightArmAngle.snapTo(0f)
                    rightArmAngle.animateTo(120f, tween(400))
                    delay(200)
                    rightArmAngle.animateTo(0f, tween(400))
                }
            }
        ) {
            Text("Bắt đầu múa võ!")
        }
    }
}

@Composable
fun EpicStickmanAnimation() {
    val scope = rememberCoroutineScope()
    var trigger by remember { mutableStateOf(false) }

    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val attackAngle = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val baseX = 100f + xOffset.value
            val baseY = size.height / 2 - yOffset.value

            // Apply rotation when jumping
            withTransform({
                translate(baseX, baseY)
                rotate(rotation.value)
            }) {
                // Head
                drawCircle(Color.Black, radius = 40f, center = Offset(0f, 0f))

                // Body
                drawLine(Color.Black, Offset(0f, 40f), Offset(0f, 140f), strokeWidth = 6f)

                // Arms
                drawLine(Color.Black, Offset(0f, 60f), Offset(-50f, 100f), strokeWidth = 6f)
                drawLine(Color.Black, Offset(0f, 60f), Offset(50f, 100f), strokeWidth = 6f)

                // Attack arm animation
                val attackArmEnd = Offset(50f * cos(Math.toRadians(attackAngle.value.toDouble()).toFloat()),
                    100f + 50f * sin(Math.toRadians(attackAngle.value.toDouble()).toFloat()))
                drawLine(Color.Red, Offset(0f, 60f), attackArmEnd, strokeWidth = 6f)

                // Legs
                drawLine(Color.Black, Offset(0f, 140f), Offset(-30f, 200f), strokeWidth = 6f)
                drawLine(Color.Black, Offset(0f, 140f), Offset(30f, 200f), strokeWidth = 6f)
            }
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            onClick = {
                if (!trigger) {
                    trigger = true
                    scope.launch {
                        // 1. Run to center
                        xOffset.animateTo(400f, tween(800))

                        // 2. Jump up
                        yOffset.animateTo(200f, tween(300))

                        // 3. Spin in air
                        rotation.animateTo(360f, tween(500))

                        // 4. Fall down
                        yOffset.animateTo(0f, tween(300))
                        rotation.snapTo(0f)

                        // 5. Attack arm swing
                        attackAngle.animateTo(160f, tween(300))
                        delay(150)
                        attackAngle.animateTo(0f, tween(300))

                        trigger = false
                    }
                }
            }
        ) {
            Text("Bắt đầu hành động!")
        }
    }
}

@Composable
fun KamehamehaDemo() {
    var triggerKame by remember { mutableStateOf(false) }
    val beamX = remember { Animatable(100f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StickmanCanvasWithKame(beamX = beamX)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                triggerKame = true
            }) {
                Text("Kamehameha!")
            }
        }

        // Launch Kamehameha beam
        LaunchedEffect(triggerKame) {
            if (triggerKame) {
                beamX.snapTo(100f)
                beamX.animateTo(
                    targetValue = 900f,
                    animationSpec = tween(500)
                )
                delay(200)
                beamX.snapTo(100f) // ẩn beam
                triggerKame = false
            }
        }
    }
}

@Composable
fun StickmanCanvasWithKame(beamX: Animatable<Float, AnimationVector1D>) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
    ) {
        // Draw Goku stickman
        val headCenter = Offset(100f, 100f)
        val bodyTop = Offset(100f, 140f)
        val bodyBottom = Offset(100f, 220f)

        drawCircle(Color.Black, radius = 40f, center = headCenter)
        drawLine(Color.Black, bodyTop, bodyBottom, strokeWidth = 6f)
        drawLine(Color.Black, Offset(100f, 150f), Offset(70f, 180f), strokeWidth = 6f) // left arm
        drawLine(Color.Black, Offset(100f, 150f), Offset(130f, 180f), strokeWidth = 6f) // right arm
        drawLine(Color.Black, Offset(100f, 220f), Offset(80f, 270f), strokeWidth = 6f) // left leg
        drawLine(Color.Black, Offset(100f, 220f), Offset(120f, 270f), strokeWidth = 6f) // right leg

        // Draw Kamehameha beam
        if (beamX.value > 100f) {
            drawLine(
                color = Color.Cyan,
                start = Offset(130f, 170f), // từ tay phải
                end = Offset(beamX.value, 170f),
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun KamehamehaEpic() {
    var trigger by remember { mutableStateOf(false) }
    val beamEndX = remember { Animatable(130f) }
    val chargeRadius = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StickmanWithKameEpic(beamEndX.value, chargeRadius.value)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { trigger = true }) {
                Text("KAMEHAMEHAAAA!!!")
            }
        }

        LaunchedEffect(trigger) {
            if (trigger) {
                // Phase 1: Charge ball
                chargeRadius.snapTo(0f)
                chargeRadius.animateTo(40f, tween(500)) // tăng dần bán kính quả cầu

                delay(300)

                // Phase 2: Fire beam
                beamEndX.snapTo(130f)
                beamEndX.animateTo(1000f, tween(600))

                delay(300)

                // Reset
                beamEndX.snapTo(130f)
                chargeRadius.snapTo(0f)
                trigger = false
            }
        }
    }
}

@Composable
fun StickmanWithKameEpic(beamEndX: Float, chargeRadius: Float) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)) {

        // Goku stickman
        val head = Offset(100f, 100f)
        val bodyTop = Offset(100f, 140f)
        val bodyBottom = Offset(100f, 220f)
        val rightHand = Offset(130f, 170f)

        drawCircle(Color.Black, 40f, head) // Head
        drawLine(Color.Black, bodyTop, bodyBottom, strokeWidth = 6f)
        drawLine(Color.Black, Offset(100f, 150f), Offset(70f, 180f), strokeWidth = 6f)
        drawLine(Color.Black, Offset(100f, 150f), rightHand, strokeWidth = 6f)
        drawLine(Color.Black, Offset(100f, 220f), Offset(80f, 270f), strokeWidth = 6f)
        drawLine(Color.Black, Offset(100f, 220f), Offset(120f, 270f), strokeWidth = 6f)

        // Charge ball (kame ball)
        if (chargeRadius > 0f) {
            drawCircle(
                color = Color.Cyan.copy(alpha = 0.6f),
                radius = chargeRadius,
                center = rightHand
            )
        }

        // Beam
        if (beamEndX > 130f) {
            drawRoundRect(
                color = Color.Cyan,
                topLeft = Offset(130f, 160f),
                size = androidx.compose.ui.geometry.Size(beamEndX - 130f, 20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
            )
        }
    }
}

@Composable
fun KamehamehaCanvas() {
    var triggerBeam by remember { mutableStateOf(false) }
    val beamX = remember { Animatable(130f) }

    // Vòng xoay tụ khí
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    LaunchedEffect(triggerBeam) {
        if (triggerBeam) {
            beamX.snapTo(130f)
            beamX.animateTo(900f, animationSpec = tween(400))
            delay(200)
            triggerBeam = false
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val centerY = 160f

            // Draw stickman
            drawCircle(Color.Black, radius = 30f, center = Offset(100f, 100f)) // Head
            drawLine(Color.Black, Offset(100f, 130f), Offset(100f, 210f), strokeWidth = 6f) // Body
            drawLine(Color.Black, Offset(100f, 150f), Offset(80f, 180f), strokeWidth = 6f) // Left arm
            drawLine(Color.Black, Offset(100f, 150f), Offset(120f, 180f), strokeWidth = 6f) // Right arm
            drawLine(Color.Black, Offset(100f, 210f), Offset(90f, 260f), strokeWidth = 6f) // Left leg
            drawLine(Color.Black, Offset(100f, 210f), Offset(110f, 260f), strokeWidth = 6f) // Right leg

            // Draw rotating energy ball
            if (!triggerBeam) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Cyan, Color.Transparent),
                        center = Offset(130f, centerY),
                        radius = 40f
                    ),
                    radius = 30f,
                    center = Offset(130f, centerY)
                )

                // Simulate glowing rotating lines
                for (i in 0..5) {
                    val rad = Math.toRadians((angle + i * 60).toDouble())
                    val x = 130f + cos(rad).toFloat() * 40f
                    val y = centerY + sin(rad).toFloat() * 40f
                    drawLine(
                        color = Color.Cyan.copy(alpha = 0.5f),
                        start = Offset(130f, centerY),
                        end = Offset(x, y),
                        strokeWidth = 2f
                    )
                }
            }

            // Draw beam
            if (triggerBeam && beamX.value > 130f) {
                val beamLength = beamX.value - 130f

                // 1. Thân tia beam to dần
                drawPath(
                    path = Path().apply {
                        moveTo(130f, centerY - 10f)
                        lineTo(130f + beamLength, centerY - 25f)
                        lineTo(130f + beamLength, centerY + 25f)
                        lineTo(130f, centerY + 10f)
                        close()
                    },
                    color = Color.Cyan.copy(alpha = 0.8f)
                )

                // 2. Đầu beam (quả cầu to ở cuối)
                drawCircle(
                    color = Color.Cyan,
                    radius = 30f,
                    center = Offset(130f + beamLength, centerY)
                )
            }
        }

        // Nút bắn beam
        Button(
            onClick = {
                triggerBeam = true
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Text("Kamehameha!")
        }
    }
}

@Composable
fun KamehamehaChargingBeam() {
    var triggerSkill by remember { mutableStateOf(false) }
    val beamX = remember { Animatable(130f) }
    val energyScale = remember { Animatable(0.1f) } // bắt đầu rất nhỏ
    var showBeam by remember { mutableStateOf(false) }

    // Vòng xoay tụ khí
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    LaunchedEffect(triggerSkill) {
        if (triggerSkill) {
            // Bước 1: Charge năng lượng (scale lên trong 5 giây)
            energyScale.snapTo(0.1f)
            energyScale.animateTo(1f, animationSpec = tween(5000))
            showBeam = true

            // Bước 2: Bắn beam, beamX chạy và giữ trong 10 giây
            beamX.snapTo(130f)
            beamX.animateTo(900f, animationSpec = tween(400))
            delay(10_000)

            // Reset lại trạng thái
            showBeam = false
            triggerSkill = false
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val centerY = 160f

            // Stickman
            drawCircle(Color.Black, radius = 30f, center = Offset(100f, 100f)) // Head
            drawLine(Color.Black, Offset(100f, 130f), Offset(100f, 210f), strokeWidth = 6f) // Body
            drawLine(Color.Black, Offset(100f, 150f), Offset(80f, 180f), strokeWidth = 6f) // Left arm
            drawLine(Color.Black, Offset(100f, 150f), Offset(120f, 180f), strokeWidth = 6f) // Right arm
            drawLine(Color.Black, Offset(100f, 210f), Offset(90f, 260f), strokeWidth = 6f) // Left leg
            drawLine(Color.Black, Offset(100f, 210f), Offset(110f, 260f), strokeWidth = 6f) // Right leg

            // Energy ball (scale during charging)
            if (!showBeam) {
                val radius = 30f * energyScale.value
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Cyan, Color.Transparent),
                        center = Offset(130f, centerY),
                        radius = radius * 1.5f
                    ),
                    radius = radius,
                    center = Offset(130f, centerY)
                )

                // Vòng xoay năng lượng
                for (i in 0..5) {
                    val rad = Math.toRadians((angle + i * 60).toDouble())
                    val x = 130f + cos(rad).toFloat() * radius * 1.3f
                    val y = centerY + sin(rad).toFloat() * radius * 1.3f
                    drawLine(
                        color = Color.Cyan.copy(alpha = 0.5f),
                        start = Offset(130f, centerY),
                        end = Offset(x, y),
                        strokeWidth = 2f
                    )
                }
            }

            // Beam
            if (showBeam && beamX.value > 130f) {
                val beamLength = beamX.value - 130f

                // Thân beam
                drawPath(
                    path = Path().apply {
                        moveTo(130f, centerY - 10f)
                        lineTo(130f + beamLength, centerY - 25f)
                        lineTo(130f + beamLength, centerY + 25f)
                        lineTo(130f, centerY + 10f)
                        close()
                    },
                    color = Color.Cyan.copy(alpha = 0.8f)
                )

                // Đầu beam
                drawCircle(
                    color = Color.Cyan,
                    radius = 30f,
                    center = Offset(130f + beamLength, centerY)
                )
            }
        }

        // Button
        Button(
            onClick = {
                if (!triggerSkill) triggerSkill = true
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Text("Kamehamehaaaa!")
        }
    }
}

@Composable
fun KamehamehaAnimation() {
    val scope = rememberCoroutineScope()
    var startBeam by remember { mutableStateOf(false) }
    val ballRadius = remember { Animatable(10f) }
    val beamAlpha = remember { Animatable(0f) }
    val beamPathOffset = 300f

    // Stickman jump offset
    val jumpOffset = remember { Animatable(0f) }

    // Trigger animation
    LaunchedEffect(Unit) {
        // Jump up
        jumpOffset.animateTo(-200f, tween(1000, easing = LinearOutSlowInEasing))

        // Start charging pose (gathering hands)
        delay(200)

        // Charging ball grows
        ballRadius.animateTo(80f, tween(5000))

        // Fire beam
        startBeam = true
        beamAlpha.animateTo(1f, tween(100))
        delay(10000)
        beamAlpha.animateTo(0f, tween(500))
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
    ) {
        val centerX = size.width / 2
        val baseY = size.height - 50f + jumpOffset.value

        // Draw Goku stickman body in a crouched charging pose
        val headCenter = Offset(centerX, baseY - 120f)
        val bodyBottom = Offset(centerX, baseY)
        val bodyTop = Offset(centerX, baseY - 80f)
        drawCircle(Color.Black, radius = 30f, center = headCenter)
        drawLine(Color.Black, bodyTop, bodyBottom, strokeWidth = 8f)

        // Arms chụm lại phía trước để tích tụ năng lượng
        val leftHand = Offset(centerX - 30f, baseY - 60f)
        val rightHand = Offset(centerX + 30f, baseY - 60f)
        drawLine(Color.Black, bodyTop, leftHand, strokeWidth = 6f)
        drawLine(Color.Black, bodyTop, rightHand, strokeWidth = 6f)

        // Legs in crouched pose
        drawLine(Color.Black, bodyBottom, Offset(centerX - 30f, baseY + 40f), strokeWidth = 6f)
        drawLine(Color.Black, bodyBottom, Offset(centerX + 30f, baseY + 40f), strokeWidth = 6f)

        // Charging ball (tích tụ tại giữa tay)
        val chargeCenter = Offset(centerX, baseY - 60f)
        drawCircle(Color.Cyan, radius = ballRadius.value, center = chargeCenter)

        // Kamehameha beam - tung chéo xuống 45 độ
        if (startBeam) {
            drawPath(
                path = Path().apply {
                    moveTo(centerX, baseY - 60f)
                    lineTo(centerX + beamPathOffset, baseY + beamPathOffset)
                },
                color = Color.Cyan.copy(alpha = beamAlpha.value),
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )
        }
    }

    // Optional: Button to restart animation (for testing)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = {
            scope.launch {
                startBeam = false
                beamAlpha.snapTo(0f)
                ballRadius.snapTo(10f)
                jumpOffset.snapTo(0f)

                jumpOffset.animateTo(-200f, tween(1000, easing = LinearOutSlowInEasing))
                delay(200)
                ballRadius.animateTo(80f, tween(5000))
                startBeam = true
                beamAlpha.animateTo(1f, tween(100))
                delay(10000)
                beamAlpha.animateTo(0f, tween(500))
            }
        }) {
            Text("Replay Kamehameha")
        }
    }
}
