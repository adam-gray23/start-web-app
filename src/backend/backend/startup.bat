echo "Creating docker image"
cd C:\Users\02nke\dcu\yr4\4yp\2024-ca400-kellyn88-graya27\src\backend\backend\
docker build --no-cache -t start-docker-image .
echo "Running docker image"
docker run -it -p 80:80 -d start-docker-image
echo "Starting server"
python3 ../manage.py runserver
