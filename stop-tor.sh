#!/bin/bash

# Auto-elevate with pkexec if not running as root
if [ "$EUID" -ne 0 ]; then
  exec pkexec "$(realpath "$0")" "$@"
fi

echo "[*] Disabling Firewall & Kill Switch..."
# Call the main stop script to clean up everything
"$(dirname "$0")"/tor-stop.sh

echo "--------------------------------------------------------"
echo "DONE! Your PC is now back to its NORMAL internet connection."
echo "Current IP Check:"
curl -s https://ipapi.co/json/ | grep -E '"ip"|"country_name"|"city"' || echo "Normal internet is active."
echo "--------------------------------------------------------"
