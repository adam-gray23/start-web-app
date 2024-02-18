import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class callDjango {
    public static void pauseCode(int line, String token) {
        try {
            // Set the URL of your Django server endpoint
            String url = "http://localhost:8000/pause-code/";

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
            // put the token in the header
            connection.setRequestProperty("X-CSRFToken", token);
            // set cookies value to the token value
            connection.setRequestProperty("Cookie", "csrftoken=" + token);
            OutputStream os = connection.getOutputStream();
            byte[] bArr = String.valueOf(line).getBytes();
            os.write(bArr);
            os.flush();
            os.close();

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
                System.out.println("API request failed with response code: " + responseCode);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printLine(String line, int currentLineNum, int currentLineLen, String token){
        try {
            // Set the URL of your Django server endpoint
            String url = "http://localhost:8000/print-line/";

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
            // put the token in the header
            connection.setRequestProperty("X-CSRFToken", token);
            // set cookies value to the token value
            connection.setRequestProperty("Cookie", "csrftoken=" + token);
            OutputStream os = connection.getOutputStream();
            String output = line + " " + currentLineNum + " " + currentLineLen;
            System.out.println(output);
            byte[] bArr = String.valueOf(output).getBytes();
            os.write(bArr);
            os.flush();
            os.close();

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
                System.out.println("API request failed with response code: " + responseCode);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //overload type with no args
    public void printLine(String line, String token){
        try {
            // Set the URL of your Django server endpoint
            String url = "http://localhost:8000/print-line/";

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
            // put the token in the header
            connection.setRequestProperty("X-CSRFToken", token);
            // set cookies value to the token value
            connection.setRequestProperty("Cookie", "csrftoken=" + token);
            OutputStream os = connection.getOutputStream();
            String output = line;
            System.out.println(output);
            byte[] bArr = String.valueOf(output).getBytes();
            os.write(bArr);
            os.flush();
            os.close();

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
                System.out.println("API request failed with response code: " + responseCode);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void endCode(String token){
        try {
            // Set the URL of your Django server endpoint
            String url = "http://localhost:8000/end-code/";

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
            // put the token in the header
            connection.setRequestProperty("X-CSRFToken", token);
            // set cookies value to the token value
            connection.setRequestProperty("Cookie", "csrftoken=" + token);

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
                System.out.println("API request failed with response code: " + responseCode);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
