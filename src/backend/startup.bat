echo "Starting Django server..."
python manage.py runserver
echo "Django server started."
echo "Starting Flask server..."
cd backend/app/
python -m flask run --host 0.0.0.0 --port 80
cd ../../
echo "Flask server started.
