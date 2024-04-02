from django.core.mail import send_mail
from django.conf import settings

def send_reset_email_to_user(subject, message, email_address):
    send_mail(
        subject,
        message,
        settings.EMAIL_HOST_USER,
        [email_address],
    )
    