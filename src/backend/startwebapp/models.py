from django.utils import timezone
from django.db import models
class Session(models.Model):
    username = models.CharField(max_length=150)
    created = models.DateTimeField(default=timezone.now())
    modified = models.DateTimeField(auto_now=True)
    title = models.CharField(max_length=50, default="Untitled")
    session = models.TextField()

    def __str__(self):
        return self.username