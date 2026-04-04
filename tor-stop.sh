#!/bin/bash

# Auto-elevate with pkexec if not running as root
if [ "$EUID" -ne 0 ]; then
  exec pkexec "$(realpath "$0")" "$@"
fi

echo "[*] Disabling Firewall & Kill Switch..."
nft delete table ip tor 2>/dev/null
nft delete table inet tor_security 2>/dev/null

echo "[*] Restoring IPv6..."
sysctl -w net.ipv6.conf.all.disable_ipv6=0 >/dev/null
sysctl -w net.ipv6.conf.default.disable_ipv6=0 >/dev/null

echo "[*] Restoring DNS settings..."
if systemctl is-enabled --quiet systemd-resolved 2>/dev/null; then
    systemctl start systemd-resolved
    ln -sf ../run/systemd/resolve/stub-resolv.conf /etc/resolv.conf
else
    echo "nameserver 1.1.1.1" > /etc/resolv.conf
fi

echo "[*] Stopping System Tor Service..."
systemctl stop tor

echo "--------------------------------------------------------"
echo "DONE! Tor is STOPPED and your normal internet is restored."
echo "Current IP Check:"
curl -s https://ipapi.co/json/ | grep -E '"ip"|"country_name"|"city"' || echo "Normal internet is active."
echo "--------------------------------------------------------"
