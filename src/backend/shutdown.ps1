echo "Shutting down server..."
docker stop redis
docker rm redis
$process = Get-Process -Id (Get-NetTCPConnection -LocalPort 8000).OwningProcess
Stop-Process -ID $process.Id -Force
echo "Server shut down."
