#!/bin/bash
# Tor Routing Reminder Script

# Wait for session to be fully loaded
sleep 5

# Show a graphical reminder using Zenity
zenity --info --title="Tor Routing Reminder" --text="Security Reminder: Your system is NOT currently routed through Tor.\n\nTo enable system-wide routing, please run:\n\n./start-tor-system.sh" --width=400 --icon-name=security-high
