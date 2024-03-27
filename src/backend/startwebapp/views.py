import json
import os
import psutil
import requests
import subprocess
import time
from django.conf import settings
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render, redirect
from channels.layers import get_channel_layer
from asgiref.sync import async_to_sync
from django.contrib.auth.forms import UserCreationForm
from .forms import RegisterUserForm, PasswordResetForm
from django.contrib.auth import authenticate, login, logout, get_user_model
from django.contrib import messages
from .models import *
from django.contrib.auth.decorators import login_required
import uuid
from rest_framework.response import Response
from rest_framework import status
from django.contrib.auth.models import User
from django.contrib.auth.tokens import default_token_generator
from django.utils.http import urlsafe_base64_encode, urlsafe_base64_decode
from django.utils.encoding import force_bytes


# Create your views here.
def home_view(request):
	return render(request, 'index.html')

def test_view(request):
	return render(request, 'test.html')

# Requests

from django.contrib import messages
from django.contrib.auth import authenticate, login
from django.shortcuts import render, redirect

def login_user(request):
    #empty the messages
    if request.method == "POST":
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)
            return redirect('home')
        else:
            messages.error(request, "Invalid username or password. Please try again.")
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

def add_uuid(request):
	if request.method == "POST":
		i = str(uuid.uuid4())
		return JsonResponse({'uuid': i})


def upload_code(request):
	if request.method == 'POST':

		text_content = request.POST.get('text_content', '')
		debugMode = request.POST.get('debugMode', '')
		breakpoints = request.POST.get('breakpoints', '')
		u = request.POST.get('uuid', '')
		token = request.headers.get('X-Csrftoken')

		suffix = u + '.txt'

		if text_content:

			try:
				working_dir = os.getcwd()
				file_path = os.path.join(working_dir, 'user-files', 'input' + suffix)
				breakpoint_path = os.path.join(working_dir, 'user-files', 'breakpoints' + suffix)

				with open(file_path, 'w') as f:
					f.write(text_content)
				with open(breakpoint_path, 'w') as f:
					f.write(breakpoints)

				java_path = os.path.join(os.environ['JAVA_HOME'], 'bin', 'java.exe')

				jar_path = os.path.join(working_dir, 'start-breakpoints.jar')

				command = [java_path, '-jar', jar_path, file_path, token, u]

				try:
					# Run the command
					process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
					

					return JsonResponse({'process': process.pid}, status=200)

				except subprocess.CalledProcessError as e:
					# Handle the case where the command fails
					return JsonResponse({'error': f'Command failed with return code {e.returncode}', 'output': e.output}, status=500)

			except requests.RequestException as e:
				# Handle request error
				return JsonResponse({'error': str(e)}, status=500)
			
def step_code(request):
	if request.method == 'POST':

		u = request.POST.get('uuid', '')
		breakpoints = request.POST.get('breakpoints', '')

		suffix = u + '.txt'

		working_dir = os.getcwd()
		file_path = os.path.join(working_dir, 'user-files', 'instruct' + suffix)
		breakpoint_path = os.path.join(working_dir, 'user-files', 'breakpoints' + suffix)
		
		with open(file_path, 'w') as f:
			f.write("continue")

		with open(breakpoint_path, 'w') as f:
			f.write(breakpoints)


		return JsonResponse({'output': 'success', 'error': ''}, status=200)
		
def pause_code(request):

	data = request.body.decode('utf-8')
	data = data.split(' ')
	i = data[1]
	line_number = data[0]

	suffix = i + '.csv'

	working_dir = os.getcwd()
	memory_path = os.path.join(working_dir, 'user-files', 'memory' + suffix)

	try:
		with open(memory_path, 'r') as f:
			memory = f.read()
	except:
		memory = ''


	layer = get_channel_layer()
	async_to_sync(layer.group_send)('breakpoint', {'type': 'send_message', 'message': line_number, 'id': i})
	async_to_sync(layer.group_send)('memory', {'type': 'send_message', 'message': memory, 'id': i})
	# send line number to frontend
	return JsonResponse({'result': line_number})

def cancel_code(request):
	pid = request.POST.get('process', '')
	u = request.POST.get('uuid', '')
	pid = int(pid)
	working_dir = os.getcwd()
	try:
		if psutil.pid_exists(pid):
			process = psutil.Process(pid)
			if process.is_running():
				# Check if the process is a JAR execution
				cmdline = process.cmdline()
				if any("java" in arg for arg in cmdline) and any(".jar" in arg for arg in cmdline):
					# Send SIGTERM signal to kill the process
					os.kill(pid, 15)
					file_path = os.path.join(working_dir, 'user-files', 'input' + u + '.txt')
					breakpoint_path = os.path.join(working_dir, 'user-files', 'breakpoints' + u + '.txt')
					memory_path = os.path.join(working_dir, 'user-files', 'memory' + u + '.csv')
					instruction_path = os.path.join(working_dir, 'user-files', 'instruct' + u + '.txt')

					os.remove(file_path)
					os.remove(breakpoint_path)
					try:
						os.remove(memory_path)
					except:
						pass
					try:
						os.remove(instruction_path)
					except:
						pass
					return JsonResponse({'message': f'Process with PID {pid} (JAR execution) killed successfully.'}, status=200)
				else:
					return JsonResponse({'error': f'Process with PID {pid} is not a JAR execution.'}, status=400)
			else:
				return JsonResponse({'error': f'Process with PID {pid} is not running.'}, status=400)
		else:
			return JsonResponse({'error': f'Process with PID {pid} does not exist.'}, status=400)
	except (ValueError, TypeError):
		return JsonResponse({'error': f'Invalid PID. {pid}'}, status=400)
	
def end_code(request): 

	i = request.body.decode('utf-8')

	working_dir = os.getcwd()
	file_path = os.path.join(working_dir, 'user-files', 'input' + i + '.txt')
	breakpoint_path = os.path.join(working_dir, 'user-files', 'breakpoints' + i + '.txt')
	memory_path = os.path.join(working_dir, 'user-files', 'memory' + i + '.csv')
	instruction_path = os.path.join(working_dir, 'user-files', 'instruct' + i + '.txt')

	# get text from memory.csv
	try:
		with open(memory_path, 'r') as f:
			memory = f.read()
	except:
		memory = ''

	layer = get_channel_layer()
	async_to_sync(layer.group_send)('memory', {'type': 'send_message', 'message': memory, 'id': i})
	async_to_sync(layer.group_send)('breakpoint', {'type': 'send_message', 'message': 'end', 'id': i})

	os.remove(file_path)
	os.remove(breakpoint_path)
	try:
		os.remove(instruction_path)
	except:
		pass
	try:
		os.remove(memory_path)
	except:
		pass

	return JsonResponse({'output': 'success', 'error': ''}, status=200)

def print_line(request):
	# get the line number from the request body
	
	data = request.body.decode('utf-8')
	data = data.split(' ')
	line = " ".join(data[:len(data)-3])
	line_number = data[len(data)-3]
	column = data[len(data)-2]
	id = data[len(data)-1]
	

	layer = get_channel_layer()
	async_to_sync(layer.group_send)(
		'print',
		{
			'type': 'send_message',
			'line': line,
			'line_number': line_number,
			'column': column,
			'id': id
		}
	)
	# send line number to frontend
	return JsonResponse({'result': line_number})

@login_required
def save_session(request):
	if request.method == 'POST':

		text_content = request.POST.get('text_content', '')
		mode = request.POST.get('mode', '')
		user = request.user

		if(mode == "save"):
			title = request.POST.get('title', '')
			session = Session(username=request.user.username, session=text_content, title=title)
			session.save()
			return JsonResponse({'result': 'success'})
		elif(mode == "overwrite"):
			title = request.POST.get('title', '')
			num = request.POST.get('num', '')
			session = Session.objects.filter(username=user.username)[int(num)]
			session.title = title
			session.session = text_content
			session.save()
			return JsonResponse({'result': 'success'})
	
@login_required
def get_sessions(request):
	if request.method == 'GET':
		user = request.user
		sessions = Session.objects.filter(username=user.username)

		if len(sessions) == 0:
			return JsonResponse({'session': []})
		
		sessionsData = []
		for session in sessions:
			sessionsData.append({
				'title': session.title,
				'created': session.created,
				'modified': session.modified,
				'session': session.session
			})

		return JsonResponse({'session': sessionsData})

def forgot_password(request):
	return render(request, 'forgot-password.html')

from .email import send_reset_email_to_user
def send_reset_email(request):
	EnterUsername = request.POST.get('username')
	print(EnterUsername)
	
	user = User.objects.filter(username=EnterUsername).first()
	print(user.password)
	print(user.email)

	if user:
		token = default_token_generator.make_token(user)
		uid = urlsafe_base64_encode(force_bytes(user.pk))
		subject = 'Password Reset Request'
		message = f'Hi {user.username},\n\nPlease click the link below to reset your password:\n\nhttp://127.0.0.1:8000/reset-password/?uid={uid}&token={token}\n\nThanks!'
		send_reset_email_to_user(subject, message, user.email)
	return redirect('email-sent')

def reset_password(request):
    uid = request.GET.get('uid')
    token = request.GET.get('token')
    
	#render the page with the uid and token
    return render(request, 'reset-password.html', {'uid': uid, 'token': token})

def update_password(request):
	uid = request.POST.get('uid')
	token = request.POST.get('token')
	new_password = request.POST.get('password2')

	try:
		uid = urlsafe_base64_decode(uid).decode()
		user = get_user_model().objects.get(pk=uid)
	except:
		user = None

	if user is not None and default_token_generator.check_token(user, token):
		user.set_password(new_password)
		user.save()

	return redirect('login')	

def email_sent(request):
	return render(request, 'email-sent.html')