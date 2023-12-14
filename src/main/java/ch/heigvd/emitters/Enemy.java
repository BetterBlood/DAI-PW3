package ch.heigvd.emitters;

import picocli.CommandLine;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(
        name = "enemy",
        description = "Start an UDP multicast emitter representing an enemy"
)
public class Enemy implements Callable<Integer> {
    @CommandLine.ParentCommand
    private ch.heigvd.Main parent;
    @CommandLine.Option(
            names = {"-pa", "--pause"},
            description = "Pause between attacks (in milliseconds) (default: 10000).",
            defaultValue = "10000"
    )
    private int pause;

    @CommandLine.Option(
            names = {"-d", "--damage"},
            description = "Frequency of sending the message (in milliseconds) (default: 10000).",
            defaultValue = "10000"
    )
    private int damage;

    @CommandLine.Option(
            names = {"-i", "--interface"},
            description = "Interface to use.",
            scope = CommandLine.ScopeType.INHERIT,
            required = true
    )
    private String interfaceName;

    @CommandLine.Option(
            names = {"-n", "--name"},
            description = "Name of the enemy.",
            defaultValue = "Enemy"
    )
    private String name;


    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(parent.getPort())) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + parent.getPort();
            System.out.println("Archer Multicast emitter started (" + myself + ")");

            InetAddress multicastAddress = InetAddress.getByName(parent.getHost());
            InetSocketAddress group = new InetSocketAddress(multicastAddress, parent.getPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            socket.joinGroup(group, networkInterface);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    String message = "ATTACK " + name + " " + damage;

                    System.out.println("Multicasting '" + message + "' to " + parent.getHost() + ":" + parent.getPort() + " on interface " + interfaceName);

                    byte[] payload = message.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket datagram = new DatagramPacket(
                            payload,
                            payload.length,
                            group
                    );

                    socket.send(datagram);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, pause, pause, TimeUnit.MILLISECONDS);

            // Keep the program running for a while
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

            socket.leaveGroup(group, networkInterface);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }
}
