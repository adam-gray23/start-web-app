from django.urls import re_path
from . import consumers

websocket_urlpatterns = [
    re_path(r'ws/breakpoint/$', consumers.BreakpointConsumer.as_asgi()),
    re_path(r'ws/print/$', consumers.PrintConsumer.as_asgi()),
    re_path(r'ws/memory/$', consumers.MemoryConsumer.as_asgi()),
]
