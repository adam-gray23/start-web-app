import json
import os
import subprocess
from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route('/')
def index():
    return 'Flask server running...'

@app.route('/upload', methods=['POST'])
def upload_file():
    print("file receved")

    file = request.form.get('file')
    debugMode = int(request.form.get('debugMode'))
    breakpoints = (request.form.get('breakpoints')).split(',')


    working_dir = os.getcwd()
    file_path = os.path.join(working_dir, 'input.txt')
    
    with open(file_path, 'w') as f:
        f.write(file)
        

    #print contents of file
    print (file)

    jar_path = os.path.join(working_dir, 'start-complete.jar')

    command = f'java -jar {jar_path} {file_path}'

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
    


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)