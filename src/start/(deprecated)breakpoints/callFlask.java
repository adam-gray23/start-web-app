import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class callFlask {
    public static void main(int line) {
        try {
            // Set the URL of your Flask server endpoint
            String url = "http://localhost:80/pause";

            // Create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Send the request
            OutputStream os = connection.getOutputStream();
            byte[] intBytes = String.valueOf(line).getBytes();
            os.write(intBytes);
            os.flush();
            os.close();

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("API request successful");
            } else {
                System.out.println("API request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
