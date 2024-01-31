import java.io.IOException;
import java.nio.file.*;

public class fileChecker {

    public static void main(String[] args) {
        // Specify the file path you want to monitor
        // get current working directory
        String currentWorkingDirectory = System.getProperty("user.dir");
        //append file path to current working directory
        String filePathString = currentWorkingDirectory + "\\instruct.txt";
        Path filePath = Paths.get(filePathString);
        try {
            // Get the file's directory
            Path dir = filePath.getParent();

            // Create a WatchService
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Register the directory for ENTRY_MODIFY events
            dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            // Run an infinite loop to continuously check for changes
            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    // Check if the event is a modification event
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // Get the context of the event (file name)
                        Path modifiedFilePath = (Path) event.context();

                        // Check if the modified file is the one you're interested in
                        if (modifiedFilePath.equals(filePath.getFileName())) {
                            // Perform your content checking logic here
                            // Example: Read the file contents and perform checks
                            String fileContent = new String(Files.readAllBytes(filePath));
                            if (fileContent.contains("continue")) {
                                key.reset();
                                return;
                            }
                        }
                    }
                }

                // Reset the key to receive further events
                key.reset();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}