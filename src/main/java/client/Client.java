package main.java.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import lombok.Getter;

import main.java.controllers.GraphicalUserInterface;

public class Client {
    private InetAddress serverAddress;
    private int serverPort;
    private ClientDatagramSocket socket;

    @Getter
    private String lastMessage = "";

    public Client(String serverAddress, String serverPort)
            throws SocketException, UnknownHostException {
        socket = new ClientDatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = Integer.parseInt(serverPort);
    }

    public void sendMessage(String message) throws IOException {
        parse(getMessage(message));
    }

    private void parse(String message) {
        var code = message.substring(0, 3);
        message = message.substring(3).trim();
        switch (code) {
            case "100":
                GraphicalUserInterface.alert.setContentText(message);
                GraphicalUserInterface.alert.showAndWait();
                break;
            case "101":
                GraphicalUserInterface.alert.setContentText(message);
                GraphicalUserInterface.alert.showAndWait();
                closeSocket();
                break;
            case "300":
                lastMessage = message;
                break;
            case "301":
                GraphicalUserInterface.alert.setContentText(message);
                GraphicalUserInterface.alert.showAndWait();
                break;
            case "302":
                lastMessage = message;
                break;
            case "303":
                lastMessage = message;
                break;
            case "304":
                GraphicalUserInterface.alert.setContentText(message);
                GraphicalUserInterface.alert.showAndWait();
                break;
            case "305":
                GraphicalUserInterface.alert.setContentText(message);
                GraphicalUserInterface.alert.showAndWait();
                break;
        }
    }

    private String getMessage(String message) throws IOException {
        socket.sendMessage(message, serverAddress, serverPort);
        return socket.getMessage();
    }

    private void closeSocket() {
        socket.close();
    }
}
