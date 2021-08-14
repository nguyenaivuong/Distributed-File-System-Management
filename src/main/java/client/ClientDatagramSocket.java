package main.java.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientDatagramSocket extends DatagramSocket {
    private static final short BUFFER_SIZE = 8192;

    public ClientDatagramSocket() throws SocketException {}

    public void sendMessage(String message, InetAddress receiverAddress, int receiverPort)
            throws IOException {
        send(new DatagramPacket(message.getBytes(), message.getBytes().length, receiverAddress,
                receiverPort));
    }

    public String getMessage() throws IOException {
        byte receiverBuffer[] = new byte[BUFFER_SIZE];
        receive(new DatagramPacket(receiverBuffer, BUFFER_SIZE));
        return new String(receiverBuffer);
    }
}
