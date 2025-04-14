1. Client side: Android Kotlin / Compose / Hilt/ OkHTTP
2. Server side:
   - "server" folder - server.py (python script for server setting up)
   - Dependencies: Type in terminal:
     + pip install fastapi uvicorn
     + pip install 'uvicorn[standard]'
     + pip install PyJWT
     + uvicorn chat_server:app --host 0.0.0.0 --port 8000 (Start server, access through url: ws://<your-ip>:8000/ws/<user_id>)
