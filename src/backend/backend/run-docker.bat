docker build -t start-docker-image . > nul 2>&1
docker run --name start -v C:\Users\02nke\dcu\yr4\4yp\2024-ca400-kellyn88-graya27\src\backend\backend\input\input.txt:/app/input.txt start-docker-image /app/input.txt > nul 2>&1
docker logs start > C:\Users\02nke\dcu\yr4\4yp\2024-ca400-kellyn88-graya27\src\backend\backend\input\output.txt
docker rm start > nul 2>&1
