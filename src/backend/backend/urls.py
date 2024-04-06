"""
URL configuration for backend project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from startwebapp import views
from django.contrib.auth import views as auth_views

urlpatterns = [
    path('', views.home_view, name='home'),
    path('code/', views.code_view, name='code'),
    path('code/<str:id>/', views.code_view_problem, name='code'),
    path('register/', views.register_user, name='register'),
    path('login/', views.login_user, name='login'),
    path('logout/', views.logout_user, name='logout'),
    path('add-uuid/', views.add_uuid, name='add-uuid'),
    path('admin/', admin.site.urls),
    path('upload-code/', views.upload_code, name='upload-code'),
    path('step-code/', views.step_code, name='step-code'),
    path('pause-code/', views.pause_code, name='pause-code'),
    path('cancel-code/', views.cancel_code, name='cancel-code'),
    path('end-code/', views.end_code, name='end-code'),
    path('print-line/', views.print_line, name='print-line'),
    path('save-session/', views.save_session, name='save-session'),
    path('get-sessions/', views.get_sessions, name='load-session'),
    path('forgot-password/', views.forgot_password, name='forgot-password'),
    path('send-reset-email/', views.send_reset_email, name='send-reset-email'),
    path('reset-password/', views.reset_password, name='reset-password'),
    path('update-password/', views.update_password, name='update-password'),
    path('email-sent/', views.email_sent, name='email-sent'),
    path('learn/', views.learn_view, name='learn'),
    path('learn/variables/', views.variables_view, name='variables'),
    path('learn/operators/', views.operators_view, name='operators'),
    path('learn/lists/', views.lists_view, name='lists'),
    path('learn/if-statements/', views.if_statements_view, name='if-statements'),
    path('learn/loops/', views.loops_view, name='loops'),
    path('learn/functions/', views.functions_view, name='functions'),
    path('learn/videos/variables', views.video_variables_view, name='videos-variables'),
    path('learn/videos/operators', views.video_operators_view, name='videos-operators'),
    path('problems/', views.problems_view, name='problems'),
]
