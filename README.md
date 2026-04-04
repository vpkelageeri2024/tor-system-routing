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

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/tor-system-routing.git
   cd tor-system-routing
   ```
2. Run the install script to set up the startup reminder:
   ```bash
   ./install.sh
   ```

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

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
