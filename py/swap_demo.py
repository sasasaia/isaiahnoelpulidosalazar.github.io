import psutil
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from matplotlib.widgets import Button
import threading
import time

class SwapDemonstrator:
    def __init__(self):
        self.time_steps = list(range(60))
        self.ram_history = [0] * 60
        self.swap_history = [0] * 60
        self.allocated_memory = []
        
        self.fig, (self.ax_ram, self.ax_swap) = plt.subplots(2, 1, figsize=(10, 8))
        self.fig.subplots_adjust(bottom=0.2, hspace=0.4)
        
        self.ram_line, = self.ax_ram.plot(self.time_steps, self.ram_history, color='dodgerblue', linewidth=2)
        self.ax_ram.set_title("Physical RAM Usage (%)")
        self.ax_ram.set_ylim(0, 100)
        self.ax_ram.grid(True, alpha=0.3)
        
        self.swap_line, = self.ax_swap.plot(self.time_steps, self.swap_history, color='orange', linewidth=2)
        self.ax_swap.set_title("Swap (Virtual RAM) Usage (%)")
        self.ax_swap.set_ylim(0, 100)
        self.ax_swap.grid(True, alpha=0.3)

        ax_add = plt.axes([0.15, 0.05, 0.3, 0.075])
        ax_clear = plt.axes([0.55, 0.05, 0.3, 0.075])
        self.btn_add = Button(ax_add, 'Allocate 1GB RAM', color='lightgreen')
        self.btn_clear = Button(ax_clear, 'Clear Allocated RAM', color='tomato')
        
        self.btn_add.on_clicked(self.allocate_memory)
        self.btn_clear.on_clicked(self.clear_memory)

        self.status_text = self.fig.text(0.5, 0.95, "Status: Monitoring...", ha="center", weight="bold")

    def allocate_memory(self, event):
        try:
            self.status_text.set_text("Allocating 1GB... please wait.")
            self.allocated_memory.append(bytearray(1024 * 1024 * 1024)) 
            self.status_text.set_text(f"Allocated: {len(self.allocated_memory)}GB")
        except MemoryError:
            self.status_text.set_text("System Refused! No more RAM/Swap available.")

    def clear_memory(self, event):
        self.allocated_memory = []
        import gc
        gc.collect()
        self.status_text.set_text("Memory Cleared. Swap should eventually decrease.")

    def update(self, frame):
        ram = psutil.virtual_memory()
        swap = psutil.swap_memory()
        
        self.ram_history.append(ram.percent)
        self.ram_history.pop(0)
        
        self.swap_history.append(swap.percent)
        self.swap_history.pop(0)
        
        self.ram_line.set_ydata(self.ram_history)
        self.swap_line.set_ydata(self.swap_history)
        
        self.ax_ram.set_ylabel(f"Used: {ram.used / (1024**3):.1f}GB / {ram.total / (1024**3):.1f}GB")
        self.ax_swap.set_ylabel(f"Used: {swap.used / (1024**3):.1f}GB / {swap.total / (1024**3):.1f}GB")
        
        return self.ram_line, self.swap_line

    def run(self):
        ani = FuncAnimation(self.fig, self.update, interval=500, cache_frame_data=False)
        plt.show()

if __name__ == "__main__":
    print("--- Swap Demonstration Tool ---")
    print("1. Watch the RAM graph as you click 'Allocate'.")
    print("2. When RAM approaches 80-90%, the OS will start moving data to 'Swap'.")
    print("3. You will see the Swap graph rise while RAM plateaus.")
    demo = SwapDemonstrator()
    demo.run()