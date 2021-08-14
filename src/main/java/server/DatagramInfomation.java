package main.java.server;

import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DatagramInfomation {
    private String message;
    private InetAddress address;
    private int port;
}
