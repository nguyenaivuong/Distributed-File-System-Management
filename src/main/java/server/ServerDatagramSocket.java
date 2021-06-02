package main.java.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerDatagramSocket extends DatagramSocket {
    public ServerDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public void sendMessage(InetAddress receiverAddress, int receiverPort, String message)
            throws IOException {
        byte buffer[] = message.getBytes();
        send(new DatagramPacket(buffer, buffer.length, receiverAddress, receiverPort));
    }

    public String getMessage() throws IOException {
        byte receiverBuffer[] = new byte[8192];
        receive(new DatagramPacket(receiverBuffer, 8192));
        return new String(receiverBuffer);
    }

    public DatagramInfomation receiveDatagramInfomation() throws IOException {
        byte receiverBuffer[] = new byte[8192];
        var datagram = new DatagramPacket(receiverBuffer, 8192);
        receive(datagram);
        return new DatagramInfomation(new String(receiverBuffer), datagram.getAddress(),
                datagram.getPort());
    }
}
