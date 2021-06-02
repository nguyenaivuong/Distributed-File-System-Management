package main.java.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformLoggingMXBean;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import org.apache.commons.io.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Cleanup;

public class Helper extends GraphicalUserInterface implements Initializable {
    private File fileToUpload;
    private ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    private TextField directory;

    @FXML
    private ListView<String> listView;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class)
                .setLoggerLevel("javafx.css", "OFF");
        try {
            updateFileList();
        } catch (IOException exception) {
        }
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setItems(observableList);
    }

    private void updateFileList() throws IOException {
        client.sendMessage("201 ");
        observableList.clear();
        String[] files = client.getLastMessage().split(";");
        for (var i = 0; i < files.length; ++i)
            observableList.add(files[i]);
    }

    private byte[] fileToByteArray(String filePath, long fileSize) throws IOException {
        byte byteArray[] = new byte[(int) fileSize];

        @Cleanup
        var fin = new FileInputStream(filePath);
        var i = 0;
        while (fin.available() != 0) {
            byteArray[i] = (byte) fin.read();
            ++i;
        }
        return byteArray;
    }

    @FXML
    private void selectFile(MouseEvent event) {
        fileToUpload = new FileChooser().showOpenDialog(closeButton.getScene().getWindow());
        if (fileToUpload != null)
            directory.setText(fileToUpload.getAbsolutePath());
    }

    @FXML
    private void preview(MouseEvent event) throws IOException {
        var selectedFile = (String) listView.getSelectionModel().getSelectedItem();
        client.sendMessage("203 " + selectedFile);
        var message = client.getLastMessage();
        if (!message.equals("File does not exist!")) {
            byte[] fileInBytes = client.getLastMessage().getBytes();
            Files.createDirectories(Paths.get("C:\\Network\\Downloads\\Cache\\"));
            var newFile = new File("C:\\Network\\Downloads\\Cache\\" + selectedFile);
            newFile.createNewFile();

            @Cleanup
            var fout = new FileOutputStream("C:\\Network\\Downloads\\Cache\\" + selectedFile);
            fout.write(fileInBytes);
            Desktop.getDesktop().open(newFile);
        } else {
            alert.setContentText(message);
            alert.showAndWait();
        }
        updateFileList();
    }

    @FXML
    private void upload(MouseEvent event) throws IOException {
        if (fileToUpload != null) {
            byte fileInBytes[];
            fileInBytes = fileToByteArray(fileToUpload.getAbsolutePath(), fileToUpload.length());
            var fileContents = new String(fileInBytes);
            client.sendMessage("200 " + fileToUpload.getName());
            client.sendMessage("202 " + fileContents);
            updateFileList();
            directory.setText("");
        }
    }

    @FXML
    private void download(MouseEvent event) throws IOException {
        var selectedFile = (String) listView.getSelectionModel().getSelectedItem();
        client.sendMessage("204 " + selectedFile);
        var message = client.getLastMessage();
        if (!message.equals("File does not exist!")) {
            byte[] fileInBytes = client.getLastMessage().getBytes();
            Files.createDirectories(Paths.get("C:\\Network\\Downloads\\"));
            var newFile = new File("C:\\Network\\Downloads\\" + selectedFile);
            newFile.createNewFile();

            @Cleanup
            var fout = new FileOutputStream("C:\\Network\\Downloads\\" + selectedFile);
            fout.write(fileInBytes);
            alert.setContentText("Your download is complete.");
            alert.showAndWait();
        } else {
            alert.setContentText(message);
            alert.showAndWait();
        }
        updateFileList();
    }

    @FXML
    private void delete(MouseEvent event) throws IOException {
        var selectedFile = (String) listView.getSelectionModel().getSelectedItem();
        client.sendMessage("205 " + selectedFile);
        var message = client.getLastMessage();
        if (message.equals("File does not exist!")) {
            alert.setContentText(message);
            alert.showAndWait();
        }
        updateFileList();
    }

    @FXML
    private void logOut(MouseEvent event) throws IOException {
        client.sendMessage("001 " + username);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(new FXMLLoader(
                getClass().getResource("/main/resources/view/GraphicalUserInterface.fxml"))
                        .load()));
        stage.show();
        ((Stage) closeButton.getScene().getWindow()).close();
        FileUtils.deleteDirectory(new File("C:\\Network\\Downloads\\Cache"));
    }
}
