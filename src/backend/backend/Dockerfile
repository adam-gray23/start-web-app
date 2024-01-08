FROM python:3.9
 
# Create app directory
WORKDIR /app
 
# Install app dependencies
COPY requirements.txt .
 
RUN pip install -r requirements.txt

COPY app.py .
 
EXPOSE 80
CMD [ "flask", "run","--host","0.0.0.0","--port","80"]
