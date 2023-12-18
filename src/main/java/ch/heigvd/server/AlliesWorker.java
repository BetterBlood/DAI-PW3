package ch.heigvd.server;

import ch.heigvd.utils.MessageType;
import ch.heigvd.utils.ProtectionType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.heigvd.utils.MessageType.ANSWER;

public class AlliesWorker implements Callable<Integer> {

    // This is new - could be passed as a parameter with picocli
    private static TowerDefense tower;

    private static final int NUMBER_OF_THREADS = 1;
    private ch.heigvd.Main parent;
    static private int port;
    static private String host;

    public AlliesWorker(TowerDefense tower, ch.heigvd.Main parent, int portu, String hostu) {
        this.tower = tower;
        this.parent = parent;
        this.port = portu;
        host = hostu;

    }

    @Override
    public Integer call() {
        // This is new - we define an executor service
        ExecutorService executor = null;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            // This is new - the executor service has a pool of threads
            executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            System.out.println("Unicast receiver started (" + myself + ")");

            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                socket.receive(packet);

                // This is new - we submit a new task to the executor service
                executor.submit(new ClientHandler(packet, myself, tower, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    // This is new - we define a new class to handle the client
    static class ClientHandler implements Runnable {

        private final DatagramPacket packet;
        private final String myself;

        private TowerDefense tower;
        private DatagramSocket socket;

        public ClientHandler(DatagramPacket packet, String myself, TowerDefense tower, DatagramSocket socket) {
            this.packet = packet;
            this.myself = myself;
            this.tower = tower;
            this.socket = socket;
        }

        @Override
        public void run() {
            String message = new String(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength(),
                    StandardCharsets.UTF_8
            );

            System.out.println("Unicast receiver (" + myself + ") received message: " + message);

            //aucun autre check parce qu'on a codé le client ou bien quand même ?
            String messageToSend = parseMessage(message);
            //nouveau paquet à renvoyer à la personne qui va expédier
            byte[] payload = messageToSend.getBytes(StandardCharsets.UTF_8);

            DatagramPacket datagram = new DatagramPacket(
                    payload,
                    payload.length,
                    packet.getAddress(),
                    packet.getPort() //connection bidirectionnelle dans ce contexte là grâce
                    // à l'information de l'expéditeur contenu dans le datagram
            );
            try {
                socket.send(datagram);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String parseMessage(String message) {
        String response = "";

        String[] splitMessage = message.split(" ");
        MessageType messageType = MessageType.findByName(splitMessage[0]);

        if (messageType == null) {
            response = "ERROR : message type not found";
            return response;
        }

        // TODO : check if message is valid
        switch (messageType) {
            case PROTECT:
                ProtectionType protectionType = ProtectionType.findByName(splitMessage[1]);
                if (protectionType == null) {
                    response = "ERROR : protection type not found";
                    return response;
                }
                switch (protectionType) {
                    case DEFEND:
                        tower.addProtection(Integer.parseInt(splitMessage[2]));
                        break;
                    case HEAL:
                        tower.heal(Integer.parseInt(splitMessage[2]));
                        break;
                }
            case GET_INFO:
                break;
        }
        response = ANSWER + " " + tower.toString();
        return response;
    }
}
