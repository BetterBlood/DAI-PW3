package ch.heigvd.server;

import picocli.CommandLine;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class EnnemiesWorker implements Callable<Integer> {
    protected ch.heigvd.Main parent;

    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Subnet range/multicast address to use.",
            required = true
    )
    
    protected String host;
    private TowerDefense tower;
   public EnnemiesWorker(TowerDefense tower) {
        this.tower = tower;
    }

    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + parent.getPort();
            System.out.println("Multicast receiver started (" + myself + ")");

            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(parent.getInterfaceName());
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

                if(msgChunks[0].toUpperCase() == "ATTACK"){
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
       tower.takeDamage(Integer.parseInt(msgChunks[2]));
    }
}
