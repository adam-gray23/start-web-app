# docker_app.py
from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/')
def index():
    return 'test'

@app.route('/upload', methods=['POST'])
def upload_file():
    uploaded_file = request.files['file']
    # Process the uploaded file as needed
    return jsonify({'message': 'File received successfully'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)