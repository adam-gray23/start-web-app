import os
import requests
import subprocess
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync

paused = True           # Bad practice, but it works for now, chnage to session storage later

# Create your views here.
def home_view(request):
    return render(request, 'index.html')

def upload_code(request):
    if request.method == 'POST':
        global paused
        paused = False

        text_content = request.POST.get('text_content', '')
        debugMode = request.POST.get('debugMode', '')
        breakpoints = request.POST.get('breakpoints', '')

        # Process and save the text content to a directory
        if text_content:
            # Define the URL where the Flask server is running
            flask_url = f'http://{settings.FLASK_HOST}:{settings.FLASK_PORT}/upload'

            try:
                # Send a POST request to the Flask server
                response = requests.post(
                    flask_url,
                    headers={'X-Csrftoken': request.headers.get('X-Csrftoken')},
                    data={
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
            
def step_code(request):
    if request.method == 'POST':
        # if not paused return 
        global paused
        if not paused:
            return JsonResponse({'result': 'not paused'})

        paused = False

        breakpoints = request.POST.get('breakpoints', '')

        # Define the URL where the Flask server is running
        flask_url = f'http://{settings.FLASK_HOST}:{settings.FLASK_PORT}/step'

        try:
            # Send a POST request to the Flask server
            response = requests.post(
                flask_url, 
                headers={'X-Csrftoken': request.headers.get('X-Csrftoken')},
                data={'breakpoints': breakpoints}
            )
            response_data = response.json()

            # Print the response body
            return JsonResponse({'result': response_data})
        except requests.RequestException as e:
            # Handle request error
            return JsonResponse({'error': str(e)}, status=500)
        
def pause_code(request):
    global paused
    paused = True
    # get line number from request body
    line_number = request.POST.get('line', '')

    layer = get_channel_layer()
    async_to_sync(layer.group_send)('test', {'type': 'send_message', 'message': line_number})
    # send line number to frontend
    return JsonResponse({'result': line_number})



