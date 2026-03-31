import socket
import json
import os

def start_server():
    # Define host and port (Termux usually uses 5000 for local apps)
    host = '127.0.0.1' 
    port = 5000

    # Create a TCP/IP socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Allow reconnecting immediately if the script crashes
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    
    server_socket.bind((host, port))
    server_socket.listen(1)
    
    print(f"Server started. Waiting for Android app on {host}:{port}...")

    while True:
        conn, addr = server_socket.accept()
        print(f"Connected by {addr}")
        
        try:
            # 1. Receive the layout sent from the App's 'init' block
            # We use a larger buffer (4096) because JSON layouts can be long
            data = conn.recv(4096).decode('utf-8').strip()
            
            if data:
                print("Received data from app. Validating JSON...")
                try:
                    layout_json = json.loads(data)
                    
                    # 2. Save the data to layout.json
                    with open("layout.json", "w") as f:
                        json.dump(layout_json, f, indent=4)

                        f.flush()
                        os.fsync(f.fileno())
                    
                    print("Success! layout.json has been updated.")
                    
                    # 3. Send a confirmation back to the app
                    conn.sendall(b"LAYOUT_RECEIVED_OK\n")
                    
                except json.JSONDecodeError:
                    print("Error: Received data was not valid JSON.")
                    conn.sendall(b"ERROR_INVALID_JSON\n")

            # Keep the connection open for logs if needed, 
            # or close it to wait for a fresh connection.
            conn.close()
            print("Connection closed. Waiting for next sync...")

        except Exception as e:
            print(f"An error occurred: {e}")
            conn.close()

if __name__ == "__main__":
    start_server()