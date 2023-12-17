package ch.heigvd.server;

import picocli.CommandLine;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class EnnemiesWorker implements Callable<Integer> {

    private TowerDefense tower;
    private String interfaceName;
    private ch.heigvd.Main parent;
    private int port;
    private String host;
   public EnnemiesWorker(TowerDefense tower, String interfaceName,ch.heigvd.Main parent , int portm, String hostm) {
        this.tower = tower;
        this.interfaceName = interfaceName;
        this.parent = parent;
        this.port = portm;
        host = hostm;
    }

    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            System.out.println("Multicast receiver started (" + myself + ")");

            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            socket.joinGroup(group, networkInterface);

            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                socket.receive(packet);

                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                String[] msgChunks = message.split(" ");

                if(msgChunks[0].equals("ATTACK")){
                    processAttack(msgChunks);
                }

                System.out.println("Multicast receiver (" + myself + ") received message: " + message);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
    private void processAttack(String [] msgChunks){
       try {
           tower.takeDamage(Integer.parseInt(msgChunks[2]));
       } catch (NumberFormatException e){
           e.printStackTrace();
       }
    }
}
