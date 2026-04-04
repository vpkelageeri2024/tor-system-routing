import tkinter as tk
from tkinter import messagebox
import subprocess
import platform
import os
import requests
import threading

class TorGui:
    def __init__(self, root):
        self.root = root
        self.root.title("Tor System Routing")
        self.root.geometry("400x300")
        self.root.resizable(False, False)

        # UI Elements
        self.label = tk.Label(root, text="Tor System Routing", font=("Arial", 16, "bold"))
        self.label.pack(pady=10)

        self.status_label = tk.Label(root, text="Status: Checking...", font=("Arial", 10))
        self.status_label.pack(pady=5)

        self.ip_label = tk.Label(root, text="IP: Fetching...", font=("Arial", 10))
        self.ip_label.pack(pady=5)

        self.start_btn = tk.Button(root, text="Start Tor Routing", command=self.start_tor, bg="#4CAF50", fg="white", width=20)
        self.start_btn.pack(pady=10)

        self.stop_btn = tk.Button(root, text="Stop Tor Routing", command=self.stop_tor, bg="#f44336", fg="white", width=20)
        self.stop_btn.pack(pady=10)

        self.refresh_btn = tk.Button(root, text="Refresh Status", command=self.update_info)
        self.refresh_btn.pack(pady=5)

        self.contact_label = tk.Label(root, text="Contact: @vpkelageri", font=("Arial", 8), fg="gray")
        self.contact_label.pack(side="bottom", pady=10)

        # Initial Update
        self.update_info()

    def update_info(self):
        def fetch():
            try:
                r = requests.get("https://ipapi.co/json/", timeout=5).json()
                ip = r.get("ip", "Unknown")
                country = r.get("country_name", "Unknown")
                self.ip_label.config(text=f"IP: {ip} ({country})")
                if country != "India": # Simplistic check for demo
                     self.status_label.config(text="Status: Tor Routing Active", fg="green")
                else:
                     self.status_label.config(text="Status: Normal Internet", fg="red")
            except:
                self.ip_label.config(text="IP: Connection Error")
                self.status_label.config(text="Status: Offline", fg="gray")

        threading.Thread(target=fetch).start()

    def run_script(self, script_name):
        try:
            if platform.system() == "Linux":
                subprocess.Popen(["./" + script_name], cwd=os.getcwd())
            elif platform.system() == "Windows":
                # Assuming Start-Tor-Windows.ps1 exists
                subprocess.Popen(["powershell.exe", "-File", script_name], cwd=os.getcwd())
            messagebox.showinfo("Action Started", f"Executing {script_name}...")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def start_tor(self):
        script = "start-tor-system.sh" if platform.system() == "Linux" else "Start-Tor-Windows.ps1"
        self.run_script(script)

    def stop_tor(self):
        script = "stop-tor.sh" if platform.system() == "Linux" else "Stop-Tor-Windows.ps1"
        self.run_script(script)

if __name__ == "__main__":
    root = tk.Tk()
    app = TorGui(root)
    root.mainloop()
