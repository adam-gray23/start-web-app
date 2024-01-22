echo "Shutting down Flask server..."
$processB = Get-Process -Id (Get-NetTCPConnection -LocalPort 80).OwningProcess
Stop-Process -ID $processB.Id -Force
echo "Flask server shut down."

sleep 5

echo "Shutting down Django server..."
$processA = Get-Process -Id (Get-NetTCPConnection -LocalPort 8000).OwningProcess
Stop-Process -ID $processA.Id -Force
echo "Django server shut down."


echo "Shutdown process completed."
