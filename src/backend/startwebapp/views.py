import os
import requests
import subprocess
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render

# Create your views here.
def home_view(request):
    return render(request, 'index.html')

# def run_shell_script():
#     # Replace 'path_to_script' with the actual path to your shell script
#     script_path = "C:/Users/02nke/dcu/yr4/4yp/2024-ca400-kellyn88-graya27/src/backend/backend/run-docker.bat"

#     # Run the shell script using subprocess
#     subprocess.call([script_path])


def upload_code(request):
    if request.method == 'POST':
        text_content = request.POST.get('text_content', '')

        # Process and save the text content to a directory
        if text_content:
            send_file_to_docker_input(text_content)

        #run_shell_script()

        return JsonResponse({'message': 'Text content saved successfully'})
    else:
        return JsonResponse({'message': 'Invalid request'}, status=400)
    
def send_file_to_docker_input(text_content):
    # Define the directory where you want to save the text files
    save_directory = "C:/Users/02nke/dcu/yr4/4yp/2024-ca400-kellyn88-graya27/src/backend/backend/input"

    # Ensure the directory exists
    if not os.path.exists(save_directory):
        os.makedirs(save_directory)

    # Generate a unique filename or use an existing one
    # You can use a timestamp or other method to create unique file names
    # In this example, we use a timestamp as the file name

    # Construct the full path for the text file
    file_path = os.path.join(save_directory, "input.txt")

    # Write the text content to the file
    with open(file_path, 'w') as file:
        file.write(text_content)

def communicate_with_docker(request):
    # Construct the URL for your Docker container
    docker_url = f'http://{settings.DOCKER_CONTAINER_HOST}:{settings.DOCKER_CONTAINER_PORT}/upload'

    # Make a request to the Docker container
    try:
        with open('C:/Users/02nke/dcu/yr4/4yp/2024-ca400-kellyn88-graya27/src/backend/backend/input/input.txt', 'rb') as file:
            files = {'file': file}
        response = requests.post(docker_url, files=files)
        response_data = response.json()  # Assuming the response is in JSON format
        # Process the data from the Docker container
        print( JsonResponse({'result': response_data}))
    except requests.RequestException as e:
        # Handle request error
        print( JsonResponse({'error': str(e)}, status=500))
