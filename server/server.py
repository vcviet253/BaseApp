from fastapi import FastAPI, WebSocket, WebSocketDisconnect, Query, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from typing import Dict, List
from pydantic import BaseModel
from datetime import datetime, timedelta
import json
import uuid
import jwt

SECRET_KEY = "your_very_secret_key"
ALGORITHM = "HS256"

app = FastAPI()

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Active WebSocket connections
active_connections: Dict[str, WebSocket] = {}

# ✅ JWT Token Utils
def create_token(user_id: str):
    payload = {
        "sub": user_id,
        "exp": datetime.utcnow() + timedelta(hours=1)
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

# ✅ Login Model
class LoginRequest(BaseModel):
    username: str
    password: str


@app.post("/login")
def login(data: LoginRequest):
    if (data.username == "user123" and data.password == "pass123") or (data.username == "user456" and data.password == "pass456"):
        token = create_token(data.username)
        return {"token": token}
    raise HTTPException(status_code=401, detail="Invalid credentials")

# ✅ Message model
class SendMessageRequest(BaseModel):
    tempId: str
    from_user: str
    to_user: str
    text: str

class WebSocketMessage(BaseModel):
    serverId: str
    tempId: str
    from_user: str
    to_user: str
    text: str
    timestamp: int

# ✅ POST to send message
@app.post("/send_message")
async def send_message(data: SendMessageRequest):
    # Simulate saving to DB and generate real serverId
    server_id = str(uuid.uuid4())
    timestamp = int(datetime.utcnow().timestamp())

    message = WebSocketMessage(
        serverId=server_id,
        tempId=data.tempId,
        from_user=data.from_user,
        to_user=data.to_user,
        text=data.text,
        timestamp=timestamp
    )

    # Push to recipient (if online)
    to_socket = active_connections.get(data.to_user)
    if to_socket:
        await to_socket.send_text(message.json())

    # Echo back to sender
    from_socket = active_connections.get(data.from_user)
    if from_socket:
        await from_socket.send_text(message.json())

    return {"status": "sent"}  # ✅ lightweight response


#Web socket handler,establishes a WebSocket connection between a client and the server
@app.websocket("/ws/{user_id}")
async def websocket_endpoint(websocket: WebSocket, user_id: str):
    await websocket.accept()
    active_connections[user_id] = websocket
    print(f"[CONNECTED] {user_id} joined.")

    try:
        while True:
            await websocket.receive_text()  # just keep alive,  FastAPI will disconnect the socket if nothing is being read => use loop to keep it open
    except WebSocketDisconnect:
        print(f"[DISCONNECTED] {user_id} left.")
        active_connections.pop(user_id, None)