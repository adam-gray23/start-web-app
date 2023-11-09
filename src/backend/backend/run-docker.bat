@echo off
setlocal

rem Record the start time
set start_time=%time%
echo Batch file started at %start_time%

@REM build the docker image silently with no output
docker build -t start-docker-image . > nul
@REM run docker image and wait for it to finish
docker run --name start start-docker-image
@REM copy docker log to output.txt
docker logs start > output.txt
@REM remove docker container silently
docker rm start > nul

rem Record the end time
set end_time=%time%
echo Batch file completed at %end_time%

rem Calculate the total running time
powershell -command "[datetime]::Parse('%end_time%') - [datetime]::Parse('%start_time%')" > time_elapsed.txt

endlocal