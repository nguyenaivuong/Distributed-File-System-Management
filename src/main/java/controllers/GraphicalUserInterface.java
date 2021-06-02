package main.java.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformLoggingMXBean;
import org.apache.commons.io.FileUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.client.Client;
import main.java.server.Server;

public class GraphicalUserInterface extends Application {
    public static Alert alert = new Alert(AlertType.INFORMATION);
    protected static Client client;
    protected static String username, serverPort, serverAddress;

    @FXML
    protected AnchorPane pane;

    @FXML
    protected ImageView closeButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField serverPortTextField;

    @FXML
    private TextField serverAddressTextField;

    @Override
    public void start(Stage stage) throws IOException {
        startServer();
        alert.setHeaderText(null);
        ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class)
                .setLoggerLevel("javafx.css", "OFF");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(FXMLLoader
                .load(getClass().getResource("/main/resources/view/GraphicalUserInterface.fxml"))));
        stage.show();
    }

    private static void startServer() {
        (new Thread() {

            @Override
            public void run() {
                try {
                    Server.getInstance();
                } catch (IOException exception) {
                }
            }
        }).start();
    }

    @FXML
    protected void closeWindow(MouseEvent event) throws IOException {
        FileUtils.deleteDirectory(new File("C:\\Network\\Downloads\\Cache"));
        System.exit(0);
    }

    @FXML
    protected void minimizeWindow(MouseEvent event) {
        ((Stage) pane.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void logIn(ActionEvent event) throws IOException {
        username = usernameTextField.getText();
        serverPort = serverPortTextField.getText();
        serverAddress = serverAddressTextField.getText();
        client = new Client(serverAddress, serverPort);
        client.sendMessage("000 " + username);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(
                new FXMLLoader(getClass().getResource("/main/resources/view/Helper.fxml")).load()));
        stage.show();
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}
