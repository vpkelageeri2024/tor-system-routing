#!/bin/bash

# Auto-elevate with pkexec if not running as root
if [ "$EUID" -ne 0 ]; then
  exec pkexec "$(realpath "$0")" "$@"
fi

echo "[*] Starting System Tor Service..."

# Check and configure /etc/tor/torrc for Transparent Proxy if needed
if ! grep -q "TransPort 9040" /etc/tor/torrc || ! grep -q "DNSPort 5354" /etc/tor/torrc; then
  echo "[!] Configuring /etc/tor/torrc for Transparent Proxy..."
  cat <<EOF >> /etc/tor/torrc
VirtualAddrNetworkIPv4 10.192.0.0/10
AutomapHostsOnResolve 1
TransPort 9040
DNSPort 5354
EOF
  systemctl restart tor
else
  systemctl start tor
fi

echo "[*] Waiting for Tor to bootstrap (Connection Progress):"
# Monitor journalctl for bootstrap progress
(
  while true; do
    PROGRESS=$(journalctl -u "tor*" -n 100 --since "1 minute ago" --no-pager | grep "Bootstrapped" | tail -n 1 | grep -oP '\d+(?=%)')
    if [ -n "$PROGRESS" ]; then
      echo -ne "    [ Progress: $PROGRESS% ]\r"
      if [ "$PROGRESS" -eq 100 ]; then
        echo -e "\n[+] Tor is 100% CONNECTED."
        break
      fi
    fi
    sleep 1
  done
)

echo "[*] Enabling System-wide Routing & Kill Switch..."
"$(dirname "$0")"/tor-route.sh

echo "--------------------------------------------------------"
echo "SUCCESS! Your entire PC is now routed through Tor."
echo "Current IP Check:"
curl -s https://ipapi.co/json/ | grep -E '"ip"|"country_name"|"city"' || echo "Check failed, but routing is active."
echo "--------------------------------------------------------"
echo "To stop: ./tor-stop.sh"
