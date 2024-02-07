import json
import os
import requests
import subprocess
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render, redirect
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.contrib.auth.forms import UserCreationForm
from .forms import RegisterUserForm
from django.contrib.auth import authenticate, login, logout
from django.contrib import messages
from .models import *
from django.contrib.auth.decorators import login_required


paused = True           # Bad practice, but it works for now, chnage to session storage later
running = False         # Change to session storage later

# Create your views here.
def home_view(request):
    return render(request, 'index.html')

# Requests

def login_user(request):
	if request.method == "POST":
		username = request.POST['username']
		password = request.POST['password']
		user = authenticate(request, username=username, password=password)
		if user is not None:
			login(request, user)
			return redirect('home')
		else:
			messages.success(request, ("There Was An Error Logging In, Try Again..."))	
			return redirect('login')	


	else:
		return render(request, 'login.html')
     
def logout_user(request):
	logout(request)
	messages.success(request, ("You Were Logged Out!"))
	return redirect('home')

def register_user(request):
	if request.method == "POST":
		form = RegisterUserForm(request.POST)
		if form.is_valid():
			form.save()
			username = form.cleaned_data['username']
			password = form.cleaned_data['password1']
			user = authenticate(username=username, password=password)
			login(request, user)
			messages.success(request, ("Registration Successful!"))
			return redirect('home')
	else:
		form = RegisterUserForm()

	return render(request, 'register.html', {'form': form })

def upload_code(request):
    if request.method == 'POST':
        global paused
        global running
        if running:
            return JsonResponse({'result': 'running'})
        
        running = True
        paused = False

        text_content = request.POST.get('text_content', '')
        debugMode = request.POST.get('debugMode', '')
        breakpoints = request.POST.get('breakpoints', '')
        token = request.headers.get('X-Csrftoken')

        # Process and save the text content to a directory
        if text_content:
            # Define the URL where the Flask server is running

            try:
                working_dir = os.getcwd()
                file_path = os.path.join(working_dir, 'input.txt')
                breakpoint_path = os.path.join(working_dir, 'breakpoints.txt')
                memory_path = os.path.join(working_dir, 'memory.csv')

                with open(file_path, 'w') as f:
                    f.write(text_content)
                with open(breakpoint_path, 'w') as f:
                    f.write(breakpoints)

                jar_path = os.path.join(working_dir, 'start-breakpoints.jar')

                command = ['java', '-jar', jar_path, file_path, token]

                try:
                    # Run the command
                    result = subprocess.run(command, capture_output=True, text=True, check=True)

                    # Process the result as needed
                    output = result.stdout
                    error = result.stderr

                    # get text from memory.csv
                    try:
                        with open(memory_path, 'r') as f:
                            memory = f.read()
                    except:
                        memory = ''

                    layer = get_channel_layer()
                    async_to_sync(layer.group_send)('memory', {'type': 'send_message', 'message': memory})

                    # You can return the output to the client or perform further processing
                    os.remove(file_path)
                    os.remove(breakpoint_path)
                    try:
                        os.remove(memory_path)
                    except:
                        pass

                    running = False

                    return JsonResponse({'output': output, 'error': error}, status=200)

                except subprocess.CalledProcessError as e:
                    # Handle the case where the command fails
                    return JsonResponse({'error': f'Command failed with return code {e.returncode}', 'output': e.output}, status=500)

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

        working_dir = os.getcwd()
        file_path = os.path.join(working_dir, 'instruct.txt')
        breakpoint_path = os.path.join(working_dir, 'breakpoints.txt')
        
        with open(file_path, 'w') as f:
            f.write("continue")

        with open(breakpoint_path, 'w') as f:
            f.write(breakpoints)


        return JsonResponse({'output': 'success', 'error': ''}, status=200)
        
def pause_code(request):
    global paused
    paused = True
    # get line number from request body

    working_dir = os.getcwd()
    memory_path = os.path.join(working_dir, 'memory.csv')

    try:
        with open(memory_path, 'r') as f:
            memory = f.read()
    except:
        memory = ''

    # get the line number from the request body
    line_number = json.loads(request.body.decode('utf-8'))

    layer = get_channel_layer()
    async_to_sync(layer.group_send)('breakpoint', {'type': 'send_message', 'message': line_number})
    async_to_sync(layer.group_send)('memory', {'type': 'send_message', 'message': memory})
    # send line number to frontend
    return JsonResponse({'result': line_number})

def print_line(request):
    # get the line number from the request body
    
    data = request.body.decode('utf-8')
    data = data.split(' ')
    line = " ".join(data[:len(data)-2])
    line_number = data[len(data)-2]
    column = data[len(data)-1]
    

    layer = get_channel_layer()
    async_to_sync(layer.group_send)(
        'print',
        {
            'type': 'send_message',
            'line': line,
            'line_number': line_number,
            'column': column
        }
    )
    # send line number to frontend
    return JsonResponse({'result': line_number})

@login_required
def save_session(request):
    if request.method == 'POST':

        text_content = request.POST.get('text_content', '')
        user = request.user

        sessions = Session.objects.filter(username=user.username)
        if len(sessions) >= 10:
            return JsonResponse({'result': 'limit reached'})

        session = Session(username=request.user.username, session=text_content)
        session.save()

        return JsonResponse({'result': 'success'})
    
@login_required
def load_session(request):
    if request.method == 'GET':
        user = request.user
        sessions = Session.objects.filter(username=user.username)

        if len(sessions) == 0:
            return JsonResponse({'session': 'none'})

        data = sessions[0].session

        return JsonResponse({'session': data})
