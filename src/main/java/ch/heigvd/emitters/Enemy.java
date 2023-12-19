package ch.heigvd.emitters;

import ch.heigvd.utils.MessageType;
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
    @CommandLine.Option(
            names = {"-pa", "--pause"},
            description = "Pause between attacks (in milliseconds) (default: 10000).",
            defaultValue = "10000"
    )
    private int pause;

    @CommandLine.Option(
            names = {"-d", "--damage"},
            description = "Damage sent to tower (default: 10).",
            defaultValue = "10"
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

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use for the mulitcast connection (default: 9876).",
            defaultValue = "9876",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int port;

    @CommandLine.Option(
            names = {"-h", "--host"},
            description = "Subnet range/multicast address to use.",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String host;


    @Override
    public Integer call() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            System.out.println(name + " as multicast emitter started (" + myself + ")");

            InetAddress multicastAddress = InetAddress.getByName(host);
            InetSocketAddress group = new InetSocketAddress(multicastAddress, port);
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            socket.joinGroup(group, networkInterface);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    String message = MessageType.getByDimOrName("att").name() + " " + name + " " + damage;

                    System.out.println("Multicasting '" + message + "' to " + host + ":" + port + " on interface " + interfaceName);

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
