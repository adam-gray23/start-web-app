echo "Starting Django server..."
Start-Process python -ArgumentList "manage.py runserver 8000" -NoNewWindow
echo "Django server started."

sleep 5

echo "Starting Flask server..."
cd backend/app/
Start-Process python -ArgumentList "-m flask run --host 0.0.0.0 --port 80" -NoNewWindow
cd ../../
echo "Flask server started."

echo "Startup process completed."
