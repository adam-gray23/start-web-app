#!/usr/env/bin bash

#run docker image and wait for it to finish
docker run --name start start-docker-image

#copy docker log to output.txt
docker logs start > input/output.txt

#remove docker container
docker rm start > /dev/null