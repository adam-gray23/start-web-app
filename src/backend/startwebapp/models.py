from django.db import models

class Session(models.Model):
    username = models.CharField(max_length=150)
    session = models.TextField()

    def __str__(self):
        return self.username