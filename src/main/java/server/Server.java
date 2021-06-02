package main.java.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import lombok.Cleanup;
import lombok.Setter;

public final class Server {
    private static volatile Server instance;
    private static final int serverPort = 1024;
    private static ServerDatagramSocket socket;
    private static String returnMessage, username;

    @Setter
    private static String filename;

    private Server() throws SocketException, IOException {
        socket = new ServerDatagramSocket(serverPort);
        socket.connect(new InetSocketAddress("www.google.com", 80));
        System.out.println("Server Address: " + socket.getLocalAddress().getHostAddress());
        System.out.println("Server Port: " + serverPort);
        socket.close();
        socket = new ServerDatagramSocket(serverPort);
        for (;;) {
            DatagramInfomation request = socket.receiveDatagramInfomation();
            parse(request.getMessage());
            socket.sendMessage(request.getAddress(), request.getPort(), returnMessage);
        }
    }

    public static Server getInstance() throws SocketException, IOException {
        Server result = instance;
        if (result != null)
            return result;
        synchronized (Server.class) {
            if (instance == null)
                instance = new Server();
            return instance;
        }
    }

    private static void parse(String message) throws IOException {
        var code = message.substring(0, 3);
        message = message.substring(3).trim();
        switch (code) {
            case "000":
                logIn(message);
                break;
            case "001":
                logOut(message);
                break;
            case "200":
                setFilename(message);
                break;
            case "201":
                listFiles();
                break;
            case "202":
                upload(message);
                break;
            case "203":
                preview(message);
            case "204":
                download(message);
                break;
            case "205":
                delete(message);
                break;
        }
    }

    private static void logIn(String message) {
        var userDir = new File("C:\\Network\\" + message);
        if (userDir.mkdirs())
            returnMessage = "100 Welcome, " + message + '!';
        else
            returnMessage = "100 Welcome back, " + message + '!';
        username = message;
    }

    private static void logOut(String mesage) {
        returnMessage = "101 Good bye, " + mesage + '!';
        username = null;
    }

    private static void listFiles() {
        var userDirectory = new File("C:\\Network\\" + username);
        File[] listOfFiles = userDirectory.listFiles();
        var sb = new StringBuilder();
        for (var i = 0; i < listOfFiles.length; ++i) {
            sb.append(listOfFiles[i].getName());
            if (i != listOfFiles.length - 1)
                sb.append(";");
        }
        var files = sb.toString();
        if (files.length() == 0)
            files = "No Files Found";
        returnMessage = "300 " + files;
    }

    private static void upload(String message) throws IOException {
        byte[] fileInBytes = message.getBytes();
        var f = new File("C:\\Network\\" + username + "\\" + filename);
        f.createNewFile();

        @Cleanup
        var fout = new FileOutputStream("C:\\Network\\" + username + "\\" + filename);
        fout.write(fileInBytes);
        returnMessage = "301 Your upload is complete.";
    }

    private static byte[] fileToByteArray(String filePath, long fileSize) throws IOException {
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

    private static void preview(String message) throws IOException {
        var f = new File("C:\\Network\\" + username + "\\" + message);
        if (f.isFile()) {
            byte[] fileInBytes = fileToByteArray(f.getAbsolutePath(), f.length());
            returnMessage = "302 " + new String(fileInBytes);
        } else
            returnMessage = "303 File does not exist!";
    }

    private static void download(String message) throws IOException {
        var f = new File("C:\\Network\\" + username + "\\" + message);
        if (f.isFile()) {
            byte[] fileInBytes = fileToByteArray(f.getAbsolutePath(), f.length());
            returnMessage = "302 " + new String(fileInBytes);
        } else
            returnMessage = "303 File does not exist!";
    }

    private static void delete(String message) {
        var f = new File("C:\\Network\\" + username + "\\" + message);
        if (f.isFile())
            if (f.delete())
                returnMessage = "304 File is deleted!";
            else
                returnMessage = "305 Cannot delete file!";
        else
            returnMessage = "303 File does not exist!";
    }
}
