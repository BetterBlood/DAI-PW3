package ch.heigvd.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static java.lang.System.exit;

public class Utils {
    public static boolean isNumeric(String s) {
        return s.matches("\\d+");
    }

}
