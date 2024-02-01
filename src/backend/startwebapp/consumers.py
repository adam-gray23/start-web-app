import os
import json
from channels.generic.websocket import AsyncWebsocketConsumer

class  TestConsumer(AsyncWebsocketConsumer):

    async def connect(self):
        self.group_name = 'test'

        await self.channel_layer.group_add(
            self.group_name,
            self.channel_name
        )

        await self.accept()

    async def disconnect(self, close_code):
        await self.channel_layer.group_discard(
            self.group_name,
            self.channel_name
        )

    async def receive(self, text_data):
        text_data_json = json.loads(text_data)

        working_dir = os.getcwd()
        file_path = os.path.join(working_dir, 'test.txt')

        with open(file_path, 'w') as f:
            f.write(text_data_json)

        message = text_data_json['message']

        await self.channel_layer.group_send(
            self.group_name,
            {
                'type': 'send_message',
                'message': message
            }
        )

    async def send_message(self, event):
        message = event['message']

        await self.send(text_data=json.dumps({
            'message': message
        }))