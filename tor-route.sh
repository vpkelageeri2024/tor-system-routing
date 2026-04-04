#!/bin/bash

# Auto-elevate with pkexec if not running as root
if [ "$EUID" -ne 0 ]; then
  exec pkexec "$(realpath "$0")" "$@"
fi

# Configuration (System 'toranon' user)
TOR_UID=$(id -u toranon 2>/dev/null || id -u tor 2>/dev/null)
if [ -z "$TOR_UID" ]; then
  echo "[!] Error: Could not find 'toranon' or 'tor' user."
  exit 1
fi
TRANS_PORT=9040
DNS_PORT=5354
NON_TOR_NETWORKS=("127.0.0.0/8" "10.0.0.0/8" "172.16.0.0/12" "192.168.0.0/16")

# 1. Setup nftables
# Flush existing Tor tables
nft delete table ip tor 2>/dev/null
nft delete table inet tor_security 2>/dev/null

# Disable IPv6 (To prevent leaks)
sysctl -w net.ipv6.conf.all.disable_ipv6=1
sysctl -w net.ipv6.conf.default.disable_ipv6=1

# --- NAT Table (Routing) ---
nft add table ip tor
nft add chain ip tor prerouting { type nat hook prerouting priority 0 \; policy accept \; }
nft add chain ip tor output { type nat hook output priority 0 \; policy accept \; }

# Redirect DNS queries to Tor's DNS port
nft add rule ip tor prerouting udp dport 53 redirect to :$DNS_PORT
nft add rule ip tor output udp dport 53 redirect to :$DNS_PORT

# DON'T redirect traffic for the user running Tor Browser (So it can reach Tor nodes)
nft add rule ip tor output skuid $TOR_UID return

# Don't redirect traffic to local networks
for net in "${NON_TOR_NETWORKS[@]}"; do
    nft add rule ip tor output ip daddr $net return
    nft add rule ip tor prerouting ip daddr $net return
done

# Redirect all other TCP traffic to Tor's TransPort
nft add rule ip tor output tcp flags \& \(syn\|rst\|ack\) == syn redirect to :$TRANS_PORT
nft add rule ip tor prerouting tcp flags \& \(syn\|rst\|ack\) == syn redirect to :$TRANS_PORT

# --- Filter Table (Kill-Switch) ---
nft add table inet tor_security
nft add chain inet tor_security output { type filter hook output priority 0 \; policy drop \; }
nft add chain inet tor_security input { type filter hook input priority 0 \; policy drop \; }

# Allow established/related traffic
nft add rule inet tor_security input ct state established,related accept
nft add rule inet tor_security output ct state established,related accept

# Allow loopback (localhost)
nft add rule inet tor_security input iifname "lo" accept
nft add rule inet tor_security output oifname "lo" accept

# Allow the user running Tor Browser to connect (Kill-Switch Bypass for Tor process)
nft add rule inet tor_security output skuid $TOR_UID accept

# Allow LAN traffic
for net in "${NON_TOR_NETWORKS[@]}"; do
    nft add rule inet tor_security output ip daddr $net accept
    nft add rule inet tor_security input ip saddr $net accept
done

# 2. DNS Settings
# Point system DNS to Tor's local DNS resolver
if systemctl is-active --quiet systemd-resolved; then
    systemctl stop systemd-resolved
    systemctl disable systemd-resolved
fi
echo "nameserver 127.0.0.1" > /etc/resolv.conf

echo "[+] Firewall & Transparent Proxy ENABLED for Tor Browser."
echo "[+] All PC traffic is now forced through Tor Browser's connection."
