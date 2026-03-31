import socket
import json
import os

def start_server():
    host = '127.0.0.1'
    port = 5000

    # 1. Create a Server Socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # This allows you to restart the script quickly without "Address already in use" errors
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    
    server_socket.bind((host, port))
    server_socket.listen(1)
    
    print(f"--- Server Started on {host}:{port} ---")
    print("Waiting for Android App to connect...")

    # 2. Wait for the Android app to open
    conn, addr = server_socket.accept()
    print(f"Connected by App at {addr}")

    try:
        while True:
            file_name = input("\nEnter JSON filename to send (or 'exit'): ").strip()

            if file_name.lower() == 'exit':
                break

            if not os.path.exists(file_name):
                print(f"Error: {file_name} not found.")
                continue

            try:
                with open(file_name, "r") as f:
                    file_data = json.load(f)

                msg_type = file_data.pop("type", "LOG")
                
                # If LAYOUT, stringify the rest. If LOG, use 'text' or stringify.
                if msg_type == "LAYOUT":
                    content_str = json.dumps(file_data, separators=(',', ':'))
                else:
                    content_str = file_data.get("text", str(file_data))

                envelope = {"type": msg_type, "content": content_str}
                packet = json.dumps(envelope, separators=(',', ':')) + "\n"

                # 3. Send through the active connection
                conn.sendall(packet.encode('utf-8'))
                print(f"SUCCESS: Sent {msg_type}")

            except Exception as e:
                print(f"JSON/File Error: {e}")

    finally:
        conn.close()
        server_socket.close()

if __name__ == "__main__":
    start_server()