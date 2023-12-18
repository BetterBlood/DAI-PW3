package ch.heigvd.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

public class Utils {
    public static boolean isNumeric(String s) {
        return s.matches("\\d+");
    }
    public static void send(String message, String timestamp, InetAddress serverAddress, DatagramSocket socket, String host, int port) throws Exception
    {
        System.out.println("Unicasting '" + message + "' to " + host + ":" + port + " at " + timestamp);

        byte[] payload = message.getBytes(StandardCharsets.UTF_8);

        DatagramPacket datagram = new DatagramPacket(
                payload,
                payload.length,
                serverAddress,
                port
        );
        System.out.print("sending... ");
        socket.send(datagram); // send data to serv
        System.out.println("data sended !");

        sleep(200);// waiting for receiving an answer (not really gorgeous)
    }
}
