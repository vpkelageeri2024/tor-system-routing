Name:           tor-system-routing
Version:        1.0.0
Release:        1%{?dist}
Summary:        System-wide Tor routing with graphical dashboard
License:        MIT
URL:            https://github.com/vishal/tor-system-routing
BuildArch:      noarch
Requires:       python3, python3-tkinter, tor, nftables, zenity, polkit

%description
Force all TCP and DNS traffic through Tor with a simple GUI.

%install
mkdir -p %{buildroot}/usr/share/tor-routing
cp /home/vishal/tor-system-routing/tor-gui.py %{buildroot}/usr/share/tor-routing/
cp /home/vishal/tor-system-routing/*.sh %{buildroot}/usr/share/tor-routing/
mkdir -p %{buildroot}/usr/bin
ln -s /usr/share/tor-routing/tor-gui.py %{buildroot}/usr/bin/tor-routing-gui

%files
/usr/share/tor-routing/*
/usr/bin/tor-routing-gui

%post
# Post-install scriptlet
echo "[*] Setting up Tor Routing..."
# Ensure Tor is installed and enabled
if command -v tor >/dev/null 2>&1; then
    systemctl enable --now tor
    echo "[+] Tor service enabled."
else
    echo "[!] Warning: Tor is not installed. Please install it using 'sudo dnf install tor'."
fi
# Set up the startup reminder for all users (or current user)
echo "[+] Setup complete. Run 'tor-routing-gui' to manage your routing."

%changelog
* Sat Apr 04 2026 Vishal - 1.0.0-1
- Initial release
