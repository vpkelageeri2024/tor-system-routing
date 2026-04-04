#!/bin/bash
# Install script for Tor Routing Reminder

PROJECT_DIR="$(pwd)"
AUTOSTART_DIR="$HOME/.config/autostart"

echo "[*] Setting up startup reminder..."
mkdir -p "$AUTOSTART_DIR"

# Create the .desktop file dynamically to use the current user's path
cat <<EOF > "$AUTOSTART_DIR/tor-reminder.desktop"
[Desktop Entry]
Type=Application
Exec=$PROJECT_DIR/tor-reminder.sh
Hidden=false
NoDisplay=false
X-GNOME-Autostart-enabled=true
Name=Tor Routing Reminder
Comment=Reminds me to start Tor routing at login.
Icon=security-high
Categories=Network;Security;
EOF

chmod +x tor-reminder.sh
chmod +x start-tor-system.sh
chmod +x stop-tor.sh
chmod +x tor-route.sh
chmod +x tor-stop.sh

echo "[+] Done! The reminder will appear next time you log in."
echo "[+] To manually start: ./start-tor-system.sh"
