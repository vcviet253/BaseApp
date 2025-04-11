from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from typing import Dict
from datetime import datetime
import json

app = FastAPI()

# Cho phép truy cập từ mọi domain (để test Android)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Lưu WebSocket của từng người dùng
active_connections: Dict[str, WebSocket] = {}

# Kiểu tin nhắn
class Message:
    def __init__(self, from_user: str, to_user: str, text: str, timestamp: int):
        self.from_user = from_user
        self.to_user = to_user
        self.text = text
        self.timestamp = timestamp

    def to_json(self):
        return json.dumps({
            "from": self.from_user,
            "to": self.to_user,
            "text": self.text,
            "timestamp": self.timestamp
        })

@app.websocket("/ws/{user_id}")
async def websocket_endpoint(websocket: WebSocket, user_id: str):
    await websocket.accept()
    active_connections[user_id] = websocket
    print(f"[CONNECTED] {user_id} joined.")

    try:
        while True:
            raw_text = await websocket.receive_text()
            print(f"[RECEIVED] From {user_id}: {raw_text}")

            # Parse message
            try:
                data = json.loads(raw_text)
                msg = Message(
                    from_user=user_id,
                    to_user=data.get("to"),
                    text=data.get("text"),
                    timestamp=int(datetime.utcnow().timestamp())
                )

                # Gửi đến người nhận nếu họ đang online
                receiver_socket = active_connections.get(msg.to_user)
                if receiver_socket:
                    await receiver_socket.send_text(msg.to_json())
                    print(f"[FORWARDED] {msg.from_user} -> {msg.to_user}")
                else:
                    print(f"[OFFLINE] {msg.to_user} not connected.")

            except Exception as e:
                print(f"[ERROR] Failed to parse/send message: {e}")

    except WebSocketDisconnect:
        print(f"[DISCONNECTED] {user_id} left.")
        del active_connections[user_id]
