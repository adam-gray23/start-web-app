import json
import os
import subprocess
from flask import Flask, jsonify, request
import requests

app = Flask(__name__)

@app.route('/')
def index():
    return 'Flask server running...'

@app.route('/upload', methods=['POST'])
def upload_file():
    print("file receved")

    token = request.headers.get('X-Csrftoken')

    file = request.form.get('file')
    debugMode = int(request.form.get('debugMode'))
    breakpoints = (request.form.get('breakpoints'))


    working_dir = os.getcwd()
    file_path = os.path.join(working_dir, 'input.txt')
    breakpoint_path = os.path.join(working_dir, 'breakpoints.txt')
    
    with open(file_path, 'w') as f:
        f.write(file)

    with open(breakpoint_path, 'w') as f:
        f.write(breakpoints)
        

    #print contents of file
    print (file)

    jar_path = os.path.join(working_dir, 'start-breakpoints.jar')

    command = f'java -jar {jar_path} {file_path} {token}'

    try:
        # Run the command
        result = subprocess.run(command, shell=False, capture_output=True, text=True, check=True)

        # Process the result as needed
        output = result.stdout
        error = result.stderr

        # You can return the output to the client or perform further processing

        return jsonify({'output': output, 'error': error}), 200

    except subprocess.CalledProcessError as e:
        # Handle the case where the command fails
        return jsonify({'error': f'Command failed with return code {e.returncode}', 'output': e.output}), 500

    finally:
        # Remove the uploaded file after processing
        os.remove(file_path)

@app.route('/step', methods=['POST'])
def step_code():
    print("step code receved")

    breakpoints = (request.form.get('breakpoints'))

    working_dir = os.getcwd()
    file_path = os.path.join(working_dir, 'instruct.txt')
    breakpoint_path = os.path.join(working_dir, 'breakpoints.txt')
    
    with open(file_path, 'w') as f:
        f.write("continue")

    with open(breakpoint_path, 'w') as f:
        f.write(breakpoints)



    return jsonify({'output': 'success', 'error': ''}), 200

@app.route('/pause', methods=['POST'])
def pause_code():
    #print the line that was sent
    data = (request.get_data().decode('utf-8')).split(" ")

    print (data[0])
    print (data[1])

    django_url = f'http://localhost:8000/pause-code/'


    # Send a POST request to the Django server
    response = requests.post(
        django_url,
        headers={'X-CSRFToken': data[1]},
        cookies={'csrftoken': data[1]},
        data={'line': data[0]}
    )
    #return success message
    return jsonify({'output': "success", 'error': "none"}), 200
    


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)