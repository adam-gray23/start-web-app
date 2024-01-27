import os
import requests
import subprocess
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render

# Create your views here.
def home_view(request):
    return render(request, 'index.html')

def upload_code(request):
    if request.method == 'POST':
        text_content = request.POST.get('text_content', '')
        debugMode = request.POST.get('debugMode', '')
        breakpoints = request.POST.get('breakpoints', '')

        # Process and save the text content to a directory
        if text_content:
            # Define the URL where the Flask server is running
            flask_url = f'http://{settings.FLASK_HOST}:{settings.FLASK_PORT}/upload'

            try:
                # Send a POST request to the Flask server
                response = requests.post(flask_url, data={
                    'file': text_content,
                    'debugMode': debugMode,
                    'breakpoints': breakpoints
                })
                response_data = response.json()

                # Print the response body
                return JsonResponse({'result': response_data})
            except requests.RequestException as e:
                # Handle request error
                return JsonResponse({'error': str(e)}, status=500)
