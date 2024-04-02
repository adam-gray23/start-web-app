clear
echo "Shutting down server..."
$process = Get-Process -Id (Get-NetTCPConnection -LocalPort 8000).OwningProcess
Stop-Process -ID $process.Id -Force
echo "Server shut down."

echo "Shutting down Redis container..."
docker stop redis
docker rm redis
echo "Redis container shut down."
