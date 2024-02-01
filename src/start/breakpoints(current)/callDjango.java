import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
<<<<<<<< HEAD:src/start/breakpoints(current)/callDjango.java
public class callDjango {
    public static void main(int line, String token) {
        try {
            // Set the URL of your Django server endpoint
            String url = "http://localhost:8000/pause-code/";
========
public class callFlask {
    public static void main(int line, String token) {
        try {
            // Set the URL of your Flask server endpoint
            String url = "http://localhost:80/pause";
>>>>>>>> dev:src/start/breakpoints(current)/callFlask.java

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
<<<<<<<< HEAD:src/start/breakpoints(current)/callDjango.java
            // put the token in the header
            connection.setRequestProperty("X-CSRFToken", token);
            // set cookies value to the token value
            connection.setRequestProperty("csrftoken", token);
            OutputStream os = connection.getOutputStream();
            byte[] bArr = String.valueOf(line).getBytes();
========
            OutputStream os = connection.getOutputStream();
            byte[] bArr = (String.valueOf(line).concat(" " + token)).getBytes();
>>>>>>>> dev:src/start/breakpoints(current)/callFlask.java
            os.write(bArr);
            os.flush();
            os.close();

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
<<<<<<<< HEAD:src/start/breakpoints(current)/callDjango.java
                System.out.println("API request successful");
========
>>>>>>>> dev:src/start/breakpoints(current)/callFlask.java
            } else {
                System.out.println("API request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}