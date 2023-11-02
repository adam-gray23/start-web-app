@REM build the docker image silently with no output
docker build -t start-docker-image . > nul
@REM run docker image and wait for it to finish
docker run --name start start-docker-image
@REM copy docker log to output.txt
docker logs start > input/output.txt
@REM remove docker container silently
docker rm start > nul