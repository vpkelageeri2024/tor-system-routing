# Tor System Routing

Force all system TCP traffic and DNS queries through the Tor network on Linux. This project provides a set of scripts to enable transparent proxying with a "Kill-Switch" to prevent data leaks.

**Developed by:** Vishal Prakash Kelageri

## Features

- **Transparent Proxying:** All TCP traffic is routed through Tor.
- **DNS Leak Protection:** System DNS is forced to use Tor's local resolver.
- **Kill-Switch:** Automatically blocks all non-Tor traffic if Tor fails.
- **IPv6 Protection:** Disables IPv6 while routing is active to prevent leaks.
- **Graphical Popups:** Uses `pkexec` and `zenity` for a modern experience.
- **Easy Elevation:** No need to type `sudo` before commands.

## Prerequisites

- **Tor:** `sudo dnf install tor` (Fedora) or `sudo apt install tor` (Ubuntu/Debian).
- **nftables:** Used for modern firewall rules.
- **Zenity:** Used for graphical reminders.

## Installation

### For All Linux Distributions (Universal)
1. Clone the repo and run the install script:
   ```bash
   git clone https://github.com/vpkelageeri2024/tor-system-routing.git
   cd tor-system-routing
   ./install.sh
   ```

### Distribution-Specific Dependencies
Before running the installer, ensure your system has the necessary base packages:

*   **Fedora:** 
    `sudo dnf install tor nftables zenity python3 python3-requests`
*   **Arch Linux:** 
    `sudo pacman -S tor nftables zenity python python-requests`
*   **Ubuntu/Debian:** 
    `sudo apt install tor nftables zenity python3 python3-requests`

## Android Standalone App

A specialized Android version of Tor System Routing is available in the `android/` directory. This app uses the Android `VpnService` API to provide full-system routing without requiring root access.

### Key Features (Android)
- **VpnService (No Root):** Routes all device traffic through Tor using a local TUN interface.
- **Bridge Support:** Includes built-in support for OBFS4 bridges to bypass network censorship.
- **Modern UI:** Built with Jetpack Compose, featuring a dynamic "Onion Shield" status and pulse animations.
- **Automatic Configuration:** Generates and manages `torrc` settings dynamically.

### How to Build and Install
1. **Clone the repository:**
   ```bash
   git clone https://github.com/vpkelageeri2024/tor-system-routing.git
   ```
2. **Open in Android Studio:**
   - Launch Android Studio.
   - Select **Open** and navigate to the `tor-system-routing/android/` directory.
   - Wait for Gradle to sync dependencies.
3. **Build the APK:**
   - Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - Once complete, the APK will be located in `app/build/outputs/apk/debug/`.
4. **Install on Device:**
   - Enable "Install from Unknown Sources" on your Android device.
   - Transfer and install the generated `.apk` file.

## Usage

### Start Tor Routing
```bash
./start-tor-system.sh
```
A popup will ask for your password. Wait for the bootstrap to reach 100%.

### Stop Tor Routing
```bash
./stop-tor.sh
```
This will restore your normal internet and re-enable IPv6.

## How it Works

1. **Tor Configuration:** The script checks your `/etc/tor/torrc` for `TransPort 9040` and `DNSPort 5354`.
2. **Firewall Rules:** Uses `nftables` to redirect all outbound TCP and DNS traffic to the Tor ports.
3. **Kill-Switch:** A high-priority filter rule drops any packet that is not routed through Tor (except for the Tor process itself).

## Support & Contact

If you encounter any issues or have questions, feel free to reach out:
- **Instagram:** [@vpkelageri](https://www.instagram.com/vpkelageri)

## License

This project is licensed under the MIT License:

Copyright (c) 2026 vpkelageeri2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
