package main.java.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerDatagramSocket extends DatagramSocket {
    private static final short BUFFER_SIZE = 8192;

    public ServerDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public void sendMessage(InetAddress receiverAddress, int receiverPort, String message)
            throws IOException {
        byte buffer[] = message.getBytes();
        send(new DatagramPacket(buffer, buffer.length, receiverAddress, receiverPort));
    }

    public String getMessage() throws IOException {
        byte receiverBuffer[] = new byte[BUFFER_SIZE];
        receive(new DatagramPacket(receiverBuffer, BUFFER_SIZE));
        return new String(receiverBuffer);
    }

    public DatagramInfomation receiveDatagramInfomation() throws IOException {
        byte receiverBuffer[] = new byte[BUFFER_SIZE];
        var datagram = new DatagramPacket(receiverBuffer, BUFFER_SIZE);
        receive(datagram);
        return new DatagramInfomation(new String(receiverBuffer), datagram.getAddress(),
                datagram.getPort());
    }
}
