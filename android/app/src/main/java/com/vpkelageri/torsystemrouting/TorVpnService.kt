package com.vpkelageri.torsystemrouting

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class TorVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    companion object {
        const val ACTION_START = "com.vpkelageri.torsystemrouting.START"
        const val ACTION_STOP = "com.vpkelageri.torsystemrouting.STOP"
        const val EXTRA_USE_BRIDGES = "EXTRA_USE_BRIDGES"
        const val EXTRA_BRIDGE_STRING = "EXTRA_BRIDGE_STRING"
        private const val TAG = "TorVpnService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val useBridges = intent.getBooleanExtra(EXTRA_USE_BRIDGES, false)
                val bridgeString = intent.getStringExtra(EXTRA_BRIDGE_STRING) ?: ""
                startVpn(useBridges, bridgeString)
            }
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn(useBridges: Boolean, bridgeString: String) {
        if (isRunning) return
        isRunning = true
        Log.i(TAG, "Starting VPN Service with Bridges: $useBridges")

        val torrcContent = generateTorrc(useBridges, bridgeString)
        saveTorrc(torrcContent)

        // Configure the TUN interface
        val builder = Builder()
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .setSession("TorVpnService")
            .setMtu(1500)

        // Route through Tor SOCKS proxy using tun2socks (placeholder logic)
        // In a real implementation, you'd start a tun2socks process here
        builder.addDnsServer("127.0.0.1") 

        vpnInterface = builder.establish()
        
        Log.d(TAG, "Torrc generated:\n$torrcContent")
        startForegroundService()
    }

    private fun generateTorrc(useBridges: Boolean, bridgeString: String): String {
        val torDataDir = File(filesDir, "tor")
        if (!torDataDir.exists()) torDataDir.mkdirs()

        val sb = StringBuilder()
        sb.append("DataDirectory ").append(torDataDir.absolutePath).append("\n")
        sb.append("SocksPort 127.0.0.1:9050\n")
        sb.append("DNSPort 127.0.0.1:5353\n")
        sb.append("TransPort 127.0.0.1:9040\n")
        
        if (useBridges && bridgeString.isNotEmpty()) {
            sb.append("UseBridges 1\n")
            // Use multiple bridge lines if provided (split by newline)
            bridgeString.split("\n").forEach { line ->
                if (line.isNotBlank()) {
                    sb.append("Bridge ").append(line.trim()).append("\n")
                }
            }
            // Placeholder for Pluggable Transport client path
            sb.append("ClientTransportPlugin obfs4 exec ./obfs4proxy\n")
        }
        
        return sb.toString()
    }

    private fun saveTorrc(content: String) {
        try {
            val torrcFile = File(filesDir, "torrc")
            FileOutputStream(torrcFile).use { it.write(content.toByteArray()) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save torrc", e)
        }
    }

    private fun startForegroundService() {
        Log.d(TAG, "VPN is now running in foreground")
    }

    private fun stopVpn() {
        isRunning = false
        vpnInterface?.close()
        vpnInterface = null
        stopSelf()
        Log.i(TAG, "VPN Service Stopped.")
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
