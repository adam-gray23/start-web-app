import subprocess
from django.http import HttpResponse
from django.shortcuts import render

# Create your views here.
def home_view(request):
    return render(request, 'index.html')

def run_shell_script(request):
    if request.method == 'POST':
        # Replace 'path_to_script' with the actual path to your shell script
        script_path = "C:\\Users\\peter\\Documents\\Work\\DCU\\case4\\project\\2024-ca400-kellyn88-graya27\\src\\backend\\backend\\run-docker.bat"

        # Run the shell script using subprocess
        subprocess.call([script_path])

    return render(request, 'index.html')
