package com.vpkelageri.torsystemrouting

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vpkelageri.torsystemrouting.ui.theme.*

@Composable
fun Dashboard(
    isConnected: Boolean,
    isConnecting: Boolean = false,
    currentIp: String,
    onToggleVpn: (Boolean) -> Unit,
    isBridgesEnabled: Boolean,
    onBridgesToggle: (Boolean) -> Unit,
    bridgeString: String,
    onBridgeStringChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        OnionShield(isConnected, isConnecting)

        Spacer(modifier = Modifier.height(48.dp))

        IpStatusCard(isConnected, currentIp)

        Spacer(modifier = Modifier.height(24.dp))

        BridgeSettingsCard(
            isBridgesEnabled = isBridgesEnabled,
            onBridgesToggle = onBridgesToggle,
            bridgeString = bridgeString,
            onBridgeStringChange = onBridgeStringChange
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onToggleVpn(!isConnected) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) Color.Red.copy(alpha = 0.7f) else OnionPurple
            ),
            shape = RoundedCornerShape(20.dp),
            enabled = !isConnecting
        ) {
            Text(
                text = if (isConnected) "STOP TOR" else if (isConnecting) "CONNECTING..." else "START TOR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OnionShield(isConnected: Boolean, isConnecting: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected || isConnecting) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isConnecting) 800 else 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isConnected || isConnecting) {
            val pulseColor = if (isConnected) NeonGreen else OnionPurple
            Canvas(modifier = Modifier.size(220.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pulseColor.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    radius = (size.minDimension / 2) * pulseScale
                )
                drawCircle(
                    color = pulseColor.copy(alpha = 0.4f),
                    radius = (size.minDimension / 2) * (if (isConnecting) pulseScale else 1f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(170.dp)
                .shadow(
                    elevation = if (isConnected) 30.dp else if (isConnecting) 15.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = OnionPurple,
                    spotColor = if (isConnected) NeonGreen else OnionPurple
                ),
            shape = CircleShape,
            color = SurfaceGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Shield Icon",
                    modifier = Modifier.size(85.dp),
                    tint = if (isConnected) NeonGreen else if (isConnecting) OnionPurple else OnionPurple.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun IpStatusCard(isConnected: Boolean, currentIp: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isConnected) 12.dp else 0.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = if (isConnected) NeonGreen.copy(alpha = 0.5f) else Color.Transparent
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isConnected) "TOR ROUTING ACTIVE" else "CONNECTION INSECURE",
                color = if (isConnected) NeonGreen else TextGray,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = currentIp,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BridgeSettingsCard(
    isBridgesEnabled: Boolean,
    onBridgesToggle: (Boolean) -> Unit,
    bridgeString: String,
    onBridgeStringChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bridge Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bypass censorship in blocked regions",
                        color = TextGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = isBridgesEnabled,
                    onCheckedChange = onBridgesToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonGreen,
                        checkedTrackColor = NeonGreen.copy(alpha = 0.3f)
                    )
                )
            }
            
            if (isBridgesEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = bridgeString,
                    onValueChange = onBridgeStringChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Custom Bridge (OBFS4)", color = TextGray) },
                    placeholder = { Text("obfs4 1.2.3.4:443 ...", color = TextGray.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = OnionPurple,
                        unfocusedBorderColor = TextGray.copy(alpha = 0.3f),
                        cursorColor = OnionPurple,
                        focusedLabelColor = OnionPurple
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
        }
    }
}
