clear
echo "Starting server..."
docker run -p 6379:6379 -d --name redis redis:5
Start-Process python -ArgumentList "manage.py runserver 8000" -NoNewWindow
sleep 5
echo "Server started."
