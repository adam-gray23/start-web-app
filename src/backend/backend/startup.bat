echo "Creating docker image"
docker build --no-cache -t start-docker-image .
echo "Running docker image"
docker run -it -p 80:80 -d start-docker-image
echo "Starting server"
python3 ../manage.py runserver
