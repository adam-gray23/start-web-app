import os

from django.contrib.auth import authenticate, get_user_model
from django.contrib.auth.models import User
from django.core import mail
from django.test import TestCase, Client
from django.urls import reverse
from .forms import RegisterUserForm
from .models import Session
from unittest.mock import MagicMock, patch


class ModelTestCase(TestCase):

    def setUp(self):
        self.client = Client()

    def test_logout_user(self):
        self.client.login(username='testuser', password='testpassword')

        response = self.client.get(reverse('logout'))
        user = authenticate(username='testuser', password='testpassword')

        self.assertEqual(response.status_code, 302)
        self.assertEqual(response.url, reverse('login'))
        self.assertIsNone(user)

    def test_register_user_POST(self):
        data = {
            'username': 'testuser',
            'password1': 'testpassword',
            'password2': 'testpassword',
            'email': 'test@test.com'
        }
        response = self.client.post(reverse('register'), data)

        self.assertEqual(response.status_code, 302)
        self.assertEqual(response.url, reverse('home'))
        self.assertTrue(User.objects.filter(username='testuser').exists())

    def test_register_user_GET(self):
        response = self.client.get(reverse('register'))

        self.assertEqual(response.status_code, 200)
        self.assertIsInstance(response.context['form'], RegisterUserForm)


    def test_login_user_invalid_credentials(self):

        user = User.objects.create_user(username='testuser', password='testpassword')

        data = {
            'username': 'testuser',
            'password': 'wrongpassword'
        }

        response = self.client.post(reverse('login'), data)
        user = authenticate(username='testuser', password='wrongpassword')

        self.assertEqual(response.status_code, 302)
        self.assertEqual(response.url, reverse('login'))
        self.assertIsNone(user) 

    def test_login_user_valid_credentials(self):

        user = User.objects.create_user(username='testuser', password='testpassword')
        
        data = {
            'username': 'testuser',
            'password': 'testpassword'
        }

        response = self.client.post(reverse('login'), data)
        user = authenticate(username='testuser', password='testpassword')

        self.assertEqual(response.status_code, 302)
        self.assertEqual(response.url, reverse('home'))
        self.assertIsNotNone(user)

    def test_add_uuid(self):
        response = self.client.post(reverse('add-uuid'))

        self.assertEqual(response.status_code, 200)
        self.assertIn('uuid', response.json())
        self.assertIsNotNone(response.json()['uuid'])

    @patch('subprocess.Popen')
    def test_upload_code(self, mock_popen):
        data = {
            'text_content': 'write ("Hello World")',
            'uuid': '1234',
            'debugMode': '1',
            'breakpoints': ''
        }
        working_dir = os.getcwd()
        java_path = os.path.join(os.environ['JAVA_HOME'], 'bin', 'java.exe')
        jar_path = os.path.join(working_dir, 'start-breakpoints.jar')
        user_files_dir = os.path.join(working_dir, 'user-files')

        mock_process = MagicMock()
        mock_process.pid = 1234
        mock_popen.return_value = mock_process

        response = self.client.post(reverse('upload-code'), data)
        
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response['Content-Type'], 'application/json')
        mock_popen.assert_called_once_with(
            [
                java_path,
                '-jar',
                jar_path,
                os.path.join(user_files_dir, 'input1234.txt'),
                None, 
                '1234'
            ],
            stdout=-1,
            stderr=-1,
            text=True
        )

        os.remove(os.path.join(user_files_dir, 'input1234.txt'))
        os.remove(os.path.join(user_files_dir, 'breakpoints1234.txt'))

    def test_step_code_POST(self):

        data = {
            'uuid': '1234',
            'breakpoints': 'breakpoint,breakpoint,breakpoint'
        }
        working_dir = os.getcwd()
        file_path = os.path.join(working_dir, 'user-files', 'instruct1234.txt')
        breakpoint_path = os.path.join(working_dir, 'user-files', 'breakpoints1234.txt')

        response = self.client.post(reverse('step-code'), data)

        self.assertEqual(response.status_code, 200)
        self.assertTrue(os.path.exists(file_path))
        self.assertTrue(os.path.exists(breakpoint_path))
        with open(file_path, 'r') as f:
            self.assertEqual(f.read(), "continue")
        with open(breakpoint_path, 'r') as f:
            self.assertEqual(f.read(), data['breakpoints'])

        os.remove(file_path)
        os.remove(breakpoint_path)

    @patch('startwebapp.views.async_to_sync')
    @patch('startwebapp.views.get_channel_layer')
    def test_pause_code_post(self, mock_get_channel_layer, mock_async_to_sync):
        mock_body_data = "10 1234"
        mock_memory_content = "Mocked memory content"
        mock_layer = MagicMock()
        mock_get_channel_layer.return_value = mock_layer

        working_dir = os.getcwd()
        memory_path = os.path.join(working_dir, 'user-files', 'memory1234.csv')
        with open(memory_path, 'w') as f:
            f.write(mock_memory_content)

        response = self.client.post(reverse('pause-code'), data=mock_body_data, content_type='text/plain')

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json(), {'result': '10'})

        os.remove(memory_path)

    @patch('os.kill')
    def test_cancel_code_POST(self, mock_os_kill):
        data = {
            'process': '123',
            'uuid': '456'
        }

        # create files for user-files directory
        working_dir = os.getcwd()
        user_files_dir = os.path.join(working_dir, 'user-files')
        os.makedirs(user_files_dir, exist_ok=True)
        with open(os.path.join(user_files_dir, 'input456.txt'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'breakpoints456.txt'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'memory456.csv'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'instruct456.txt'), 'w') as f:
            f.write('some content')

        with patch('psutil.pid_exists', return_value=True):
            with patch('psutil.Process') as mock_process:
                mock_process.return_value.is_running.return_value = True
                mock_process.return_value.cmdline.return_value = ['java', '-jar', 'example.jar']

                response = self.client.post(reverse('cancel-code'), data)

                self.assertEqual(response.status_code, 200)
                self.assertJSONEqual(response.content, {'message': 'Process with PID 123 (JAR execution) killed successfully.'})
                self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'input456.txt')))
                self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'breakpoints456.txt')))
                self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'memory456.csv')))
                self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'instruct456.txt')))
                mock_os_kill.assert_called_once_with(123, 15)

    @patch('startwebapp.views.async_to_sync')
    @patch('startwebapp.views.get_channel_layer')
    def test_end_code_POST(self, mock_get_channel_layer, mock_async_to_sync):
        mock_body_data = "1234"
        mock_layer = MagicMock()
        mock_get_channel_layer.return_value = mock_layer

        working_dir = os.getcwd()
        user_files_dir = os.path.join(working_dir, 'user-files')
        os.makedirs(user_files_dir, exist_ok=True)
        with open(os.path.join(user_files_dir, 'input1234.txt'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'breakpoints1234.txt'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'memory1234.csv'), 'w') as f:
            f.write('some content')
        with open(os.path.join(user_files_dir, 'instruct1234.txt'), 'w') as f:
            f.write('some content')

        response = self.client.post(reverse('end-code'), data=mock_body_data, content_type='text/plain')

        self.assertEqual(response.status_code, 200)
        self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'input1234.txt')))
        self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'breakpoints1234.txt')))
        self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'memory1234.csv')))
        self.assertFalse(os.path.exists(os.path.join(user_files_dir, 'instruct1234.txt')))

    @patch('startwebapp.views.async_to_sync')
    @patch('startwebapp.views.get_channel_layer')
    def test_print_line_POST(self, mock_get_channel_layer, mock_async_to_sync):
        mock_body_data = "Hello World 1 1 id"

        response = self.client.post(reverse('print-line'), data=mock_body_data, content_type='text/plain')

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json(), {'result': '1'})

    def test_save_session_view_save_mode(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client.login(username='testuser', password='testpassword')

        data = {
            'text_content': 'Test content',
            'mode': 'save', 
            'title': 'Test Title'
        }
        
        response = self.client.post(reverse('save-session'), data)

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response['Content-Type'], 'application/json')
        self.assertTrue(Session.objects.filter(username=self.user.username, title='Test Title').exists())

    def test_save_session_view_overwrite_mode(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client.login(username='testuser', password='testpassword')
        session = Session.objects.create(username=self.user.username, session='Old content', title='Old Title')

        data = {
            'text_content': 'New content',
            'mode': 'overwrite',
            'title': 'New Title',
            'num': 0
        
        }

        response = self.client.post(reverse('save-session'), data)

        new_session = Session.objects.get(username=self.user.username, title='New Title')

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response['Content-Type'], 'application/json')
        self.assertEqual(new_session.session, 'New content')
        self.assertEqual(new_session.title, 'New Title')

    def test_get_sessions_GET(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client.login(username='testuser', password='testpassword')
        Session.objects.create(username=self.user.username, session='Test content', title='Test Title')

        response = self.client.get(reverse('load-session'))

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response['Content-Type'], 'application/json')
        self.assertIn('session', response.json())
        self.assertEqual(len(response.json()['session']), 1)

    @patch('startwebapp.email.send_reset_email_to_user')
    def test_send_reset_email(self, mock_send_reset_email_to_user):
        self.user = User.objects.create_user(username='testuser', password='testpassword', email='test@test.com')
        data = {'username': 'testuser'}

        with patch('django.contrib.auth.models.User.objects.filter') as mock_user_filter:
            mock_user_filter.return_value.first.return_value = self.user

            response = self.client.post(reverse('send-reset-email'), data=data)

            self.assertRedirects(response, reverse('email-sent'))
            self.assertEqual(len(mail.outbox), 1)  # Ensure email is sent
            self.assertIn('Password Reset Request', mail.outbox[0].subject)
            self.assertIn('Hi testuser', mail.outbox[0].body)
            self.assertIn('http://127.0.0.1:8000/reset-password/', mail.outbox[0].body)

    @patch('django.utils.http.urlsafe_base64_decode')
    @patch('django.contrib.auth.tokens.default_token_generator.check_token')
    def test_update_password_view(self, mock_check_token, mock_urlsafe_base64_decode):
        self.user = User.objects.create_user(username='testuser', password='testpassword')

        data = {
            'uid': 'dXNlcm5hbWU=',
            'token': 'validtoken',
            'password2': 'newpassword'
        }

        mock_urlsafe_base64_decode.return_value.decode.return_value = 'testuser'

        with patch.object(get_user_model().objects, 'get') as mock_get_user:
            mock_get_user.return_value = self.user
            mock_check_token.return_value = True

            response = self.client.post(reverse('update-password'), data)

            self.user.refresh_from_db()

            self.assertRedirects(response, reverse('login'))
            self.assertTrue(self.user.check_password('newpassword'))
