docker build -t start-docker-image . > nul 2>&1
docker run --name start start-docker-image > nul 2>&1
docker logs start > C:\Users\peter\Documents\Work\DCU\case4\project\2024-ca400-kellyn88-graya27\src\backend\backend\input\output.txt
docker rm start > nul 2>&1
