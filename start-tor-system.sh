#!/bin/bash

# Auto-elevate with pkexec if not running as root
if [ "$EUID" -ne 0 ]; then
  exec pkexec "$(realpath "$0")" "$@"
fi

echo "[*] Starting System Tor Service..."
systemctl start tor

echo "[*] Waiting for Tor to bootstrap (Connection Progress):"
# Monitor journalctl for bootstrap progress
(
  while true; do
    PROGRESS=$(journalctl -u tor.service -n 100 --no-pager | grep "Bootstrapped" | tail -n 1 | grep -oP '\d+(?=%)')
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
