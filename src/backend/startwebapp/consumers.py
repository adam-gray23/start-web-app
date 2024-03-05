import os
import json
from channels.generic.websocket import AsyncWebsocketConsumer

class BreakpointConsumer(AsyncWebsocketConsumer):

    async def connect(self):
        self.group_name = 'breakpoint'

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
        id = event['id']

        await self.send(text_data=json.dumps({
            'message': message,
            'id': id
        }))

class PrintConsumer(AsyncWebsocketConsumer):
        async def connect(self):
            self.group_name = 'print'
    
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
    
            message = text_data_json['message']
    
            await self.channel_layer.group_send(
                self.group_name,
                {
                    'type': 'send_message',
                    'message': message
                }
            )
    
        async def send_message(self, event):
            line = event['line']
            line_number = event['line_number']
            column = event['column']
            id = event['id']
    
            await self.send(text_data=json.dumps({
                'line': line,
                'line_number': line_number,
                'column': column,
                'id': id
            }))

class MemoryConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.group_name = 'memory'

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
        id = event['id']

        await self.send(text_data=json.dumps({
            'message': message,
            'id': id
        }))

class ProcessConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.group_name = 'process'

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
        id = event['id']

        await self.send(text_data=json.dumps({
            'message': message,
            'id': id
        }))