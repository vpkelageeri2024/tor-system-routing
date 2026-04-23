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
# Monitor journalctl for bootstrap progress with a 120s timeout
MAX_WAIT=120
WAIT_TIME=0
while [ $WAIT_TIME -lt $MAX_WAIT ]; do
  # Get latest bootstrap progress from journalctl without time filter to detect current state
  PROGRESS=$(journalctl -u "tor*" -n 200 --no-pager | grep "Bootstrapped" | tail -n 1 | grep -oP '\d+(?=%)')
  
  if [ -n "$PROGRESS" ]; then
    echo -ne "    [ Progress: $PROGRESS% ]\r"
    if [ "$PROGRESS" -eq 100 ]; then
      echo -e "\n[+] Tor is 100% CONNECTED."
      break
    fi
  else
    echo -ne "    [ Waiting for Tor to report status... ]\r"
  fi
  
  sleep 2
  WAIT_TIME=$((WAIT_TIME + 2))
done

if [ $WAIT_TIME -ge $MAX_WAIT ]; then
  echo -e "\n[!] Warning: Tor bootstrap is taking too long. Check 'journalctl -u tor' for details."
  echo "[!] If you are in a restricted network, consider using bridges in /etc/tor/torrc."
fi

echo "[*] Enabling System-wide Routing & Kill Switch..."
"$(dirname "$0")"/tor-route.sh

echo "--------------------------------------------------------"
echo "SUCCESS! Your entire PC is now routed through Tor."
echo "Current IP Check:"
curl -s http://ip-api.com/json/ | grep -oP '"(query|country|city)":"[^"]+"' | sed 's/"//g' | sed 's/:/: /'
echo "--------------------------------------------------------"
echo "To stop: ./tor-stop.sh"
