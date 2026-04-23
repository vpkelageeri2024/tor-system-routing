import tkinter as tk
from tkinter import messagebox, scrolledtext
import subprocess
import platform
import os
import requests
import threading
import time
import re

class TorGui:
    def __init__(self, root):
        self.root = root
        self.root.title("Tor System Routing")
        self.root.geometry("500x550")
        self.root.resizable(False, False)

        # UI Elements
        self.label = tk.Label(root, text="Tor System Routing", font=("Arial", 18, "bold"))
        self.label.pack(pady=10)

        self.status_frame = tk.Frame(root)
        self.status_frame.pack(pady=5)

        self.status_label = tk.Label(self.status_frame, text="Status: Checking...", font=("Arial", 11))
        self.status_label.pack(side="left")

        self.ip_label = tk.Label(root, text="IP: Fetching...", font=("Arial", 10))
        self.ip_label.pack(pady=5)

        # Progress Bar (Simplified with Label)
        self.progress_label = tk.Label(root, text="Bootstrap: 0%", font=("Arial", 10, "italic"), fg="blue")
        self.progress_label.pack(pady=5)

        self.btn_frame = tk.Frame(root)
        self.btn_frame.pack(pady=10)

        self.start_btn = tk.Button(self.btn_frame, text="Start Tor Routing", command=self.start_tor, bg="#4CAF50", fg="white", width=20)
        self.start_btn.grid(row=0, column=0, padx=5)

        self.stop_btn = tk.Button(self.btn_frame, text="Stop Tor Routing", command=self.stop_tor, bg="#f44336", fg="white", width=20)
        self.stop_btn.grid(row=0, column=1, padx=5)

        # Log Window
        self.log_area = scrolledtext.ScrolledText(root, height=10, width=55, font=("Courier", 9))
        self.log_area.pack(pady=10, padx=10)
        self.log_area.insert(tk.END, "Waiting for action...\n")
        self.log_area.configure(state='disabled')

        self.refresh_btn = tk.Button(root, text="Refresh Status", command=self.update_info)
        self.refresh_btn.pack(pady=5)

        self.contact_label = tk.Label(root, text="Contact: @vpkelageri", font=("Arial", 8), fg="gray")
        self.contact_label.pack(side="bottom", pady=10)

        self.is_monitoring = False
        
        # Initial Update
        self.update_info()

    def log(self, message):
        self.log_area.configure(state='normal')
        self.log_area.insert(tk.END, f"[{time.strftime('%H:%M:%S')}] {message}\n")
        self.log_area.see(tk.END)
        self.log_area.configure(state='disabled')

    def update_info(self):
        def fetch():
            try:
                # Use a specific timeout to avoid hanging the UI
                r = requests.get("https://ipapi.co/json/", timeout=5).json()
                ip = r.get("ip", "Unknown")
                country = r.get("country_name", "Unknown")
                org = r.get("org", "Unknown")
                
                self.ip_label.config(text=f"IP: {ip} | {country}")
                self.log(f"Current Provider: {org}")
                
                if "Tor" in org or country != "India": # Simplistic check
                     self.status_label.config(text="Status: Tor Routing Active", fg="green")
                else:
                     self.status_label.config(text="Status: Normal Internet", fg="red")
            except Exception as e:
                self.ip_label.config(text="IP: Connection Error")
                self.status_label.config(text="Status: Offline", fg="gray")
                self.log(f"Status check failed: {str(e)}")

        threading.Thread(target=fetch, daemon=True).start()

    def monitor_bootstrap(self):
        self.is_monitoring = True
        self.log("Monitoring Tor bootstrap progress...")
        
        def monitor():
            while self.is_monitoring:
                try:
                    # Run journalctl to get the latest bootstrap progress
                    result = subprocess.run(
                        ["journalctl", "-u", "tor*", "-n", "100", "--no-pager"],
                        capture_output=True, text=True
                    )
                    matches = re.findall(r"Bootstrapped (\d+)%", result.stdout)
                    if matches:
                        progress = matches[-1]
                        self.progress_label.config(text=f"Bootstrap: {progress}%")
                        if progress == "100":
                            self.log("Tor reported 100% Bootstrap.")
                            self.update_info()
                            break
                    time.sleep(2)
                except Exception as e:
                    self.log(f"Monitor error: {e}")
                    break
            self.is_monitoring = False

        threading.Thread(target=monitor, daemon=True).start()

    def run_script(self, script_name):
        try:
            self.log(f"Executing {script_name}...")
            if platform.system() == "Linux":
                # Using pkexec for terminal-based scripts if they need elevation
                # But start-tor-system.sh already has self-elevation
                subprocess.Popen(["bash", "./" + script_name], cwd=os.getcwd())
            elif platform.system() == "Windows":
                subprocess.Popen(["powershell.exe", "-File", script_name], cwd=os.getcwd())
            
            if "start" in script_name:
                self.monitor_bootstrap()
                
        except Exception as e:
            messagebox.showerror("Error", str(e))
            self.log(f"Error: {e}")

    def start_tor(self):
        script = "start-tor-system.sh" if platform.system() == "Linux" else "Start-Tor-Windows.ps1"
        self.run_script(script)

    def stop_tor(self):
        self.is_monitoring = False
        script = "stop-tor.sh" if platform.system() == "Linux" else "Stop-Tor-Windows.ps1"
        self.run_script(script)
        self.progress_label.config(text="Bootstrap: 0%")

if __name__ == "__main__":
    root = tk.Tk()
    app = TorGui(root)
    root.mainloop()
