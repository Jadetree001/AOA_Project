import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class FileBrowser extends Application {

    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sophisticated File Browser");

        // Layout setup
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button browseButton = new Button("Browse Folder");
        outputArea = new TextArea();
        outputArea.setEditable(false); // Prevent editing of the output

        browseButton.setOnAction(e -> browseFolder(primaryStage));

        layout.getChildren().addAll(browseButton, new Label("File Details:"), outputArea);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void browseFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            listFiles(selectedDirectory);
        } else {
            outputArea.setText("No folder selected.");
        }
    }

    private void listFiles(File directory) {
        outputArea.clear();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    outputArea.appendText(getFileDetails(file) + "\n\n");
                } catch (IOException e) {
                    outputArea.appendText("Error reading file: " + file.getName() + "\n\n");
                }
            }
        } else {
            outputArea.setText("No files found in the selected directory.");
        }
    }

    private String getFileDetails(File file) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(file.getName());
        details.append("\nSize: ").append(file.length()).append(" bytes");
        details.append("\nLast Modified: ").append(new Date(file.lastModified()));

        if (!attributes.isDirectory()) {
            details.append("\nType: ").append(getFileType(file));
            details.append("\nSummary: ").append(summarizeContent(file));
        } else {
            details.append("\nType: Directory");
        }

        return details.toString();
    }

    private String getFileType(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] magicBytes = new byte[8]; // Read the first 8 bytes for magic number
            fis.read(magicBytes);

            // Convert magic bytes to a hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : magicBytes) {
                hexString.append(String.format("%02X ", b));
            }
            String magicNumber = hexString.toString().trim();

            // Check magic numbers for known file types
            if (magicNumber.startsWith("25 50 44 46")) {
                return "PDF Document";
            } else if (magicNumber.startsWith("FF D8 FF")) {
                return "JPEG Image";
            } else if (magicNumber.startsWith("89 50 4E 47")) {
                return "PNG Image";
            } else if (magicNumber.startsWith("50 4B 03 04")) {
                return "ZIP Archive";
            } else if (magicNumber.startsWith("49 44 33")) {
                return "MP3 Audio";
            } else {
                return "Unknown (Magic Number: " + magicNumber + ")";
            }
        } catch (IOException e) {
            return "Error detecting file type.";
        }
    }

    private String summarizeContent(File file) {
        String extension = getFileExtension(file.getName());
        switch (extension) {
            case "txt":
                return summarizeTextFile(file);
            case "pdf":
                return "PDF summarization not yet implemented.";
            case "jpg":
            case "png":
                return "Image description not yet implemented.";
            case "mp4":
                return "Video summarization not yet implemented.";
            default:
                return "No summary available.";
        }
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index > 0) ? fileName.substring(index + 1).toLowerCase() : "";
    }

    private String summarizeTextFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder summary = new StringBuilder();
            String line;
            int lines = 0;

            while ((line = reader.readLine()) != null && lines < 5) {
                summary.append(line).append("\n");
                lines++;
            }

            return summary.toString().trim();
        } catch (IOException e) {
            return "Error reading text file.";
        }
    }
}
