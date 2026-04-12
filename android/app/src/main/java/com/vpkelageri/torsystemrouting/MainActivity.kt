package com.vpkelageri.torsystemrouting

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.vpkelageri.torsystemrouting.ui.theme.TorSystemRoutingTheme

class MainActivity : ComponentActivity() {

    private var isVpnActive by mutableStateOf(false)
    private var isConnecting by mutableStateOf(false)
    private var currentIp by mutableStateOf("127.0.0.1")
    private var isBridgesEnabled by mutableStateOf(false)
    private var bridgeString by mutableStateOf("")

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnService()
        } else {
            isConnecting = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TorSystemRoutingTheme {
                Dashboard(
                    isConnected = isVpnActive,
                    isConnecting = isConnecting,
                    currentIp = currentIp,
                    onToggleVpn = { shouldStart ->
                        if (shouldStart) {
                            isConnecting = true
                            prepareVpn()
                        } else {
                            stopVpnService()
                        }
                    },
                    isBridgesEnabled = isBridgesEnabled,
                    onBridgesToggle = { isBridgesEnabled = it },
                    bridgeString = bridgeString,
                    onBridgeStringChange = { bridgeString = it }
                )
            }
        }
    }

    private fun prepareVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            startVpnService()
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, TorVpnService::class.java).apply {
            action = TorVpnService.ACTION_START
            putExtra(TorVpnService.EXTRA_USE_BRIDGES, isBridgesEnabled)
            putExtra(TorVpnService.EXTRA_BRIDGE_STRING, bridgeString)
        }
        startService(intent)
        
        // Simulate connection delay for UI feedback
        Handler(Looper.getMainLooper()).postDelayed({
            isVpnActive = true
            isConnecting = false
            currentIp = "185.220.101.10 (Tor)" // Simulated Tor exit IP
        }, 3000)
    }

    private fun stopVpnService() {
        val intent = Intent(this, TorVpnService::class.java).apply {
            action = TorVpnService.ACTION_STOP
        }
        startService(intent)
        isVpnActive = false
        isConnecting = false
        currentIp = "127.0.0.1"
    }
}
