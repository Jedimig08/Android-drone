import socket
import time

HOST = '127.0.0.1'
PORT = 5000

def start_server():
    # Create a TCP/IP socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        # Allow immediate reuse of the port after closing
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind((HOST, PORT))
        s.listen(1)
        print(f"Server listening on {HOST}:{PORT}...")
        
        conn, addr = s.accept()
        with conn:
            print(f"Connected by {addr}")
            count = 0
            try:
                while True:
                    # This is the text your Android app will receive
                    message = f"Log Entry #{count}: System is nominal\n"
                    conn.sendall(message.encode())
                    print(f"Sent: {message.strip()}")
                    
                    count += 1
                    time.sleep(2) # Send a message every 2 seconds
            except BrokenPipeError:
                print("App disconnected.")

if __name__ == "__main__":
    start_server()
