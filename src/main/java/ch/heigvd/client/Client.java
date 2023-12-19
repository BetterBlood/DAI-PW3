package ch.heigvd.client;

import ch.heigvd.utils.MessageType;
import ch.heigvd.utils.ProtectionType;
import ch.heigvd.utils.Utils;
import picocli.CommandLine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static ch.heigvd.utils.MessageType.getByDimOrName;
import static java.lang.System.exit;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Tower Defense")
public class Client implements Callable<Integer> {
    private final Scanner sc = new Scanner(System.in);
    private final SimpleDateFormat dateFormat;
    private final static String helpClient =    "Help Command Menu [Case Insensitive]:\n" +
                                                "\th, H, help, HELP : this menu\n" +
                                                "\tpro h 10 : heal with 10\n" +
                                                "\tprotect heal 15 : heal with 15\n" +
                                                "\tpro d 20 : defend with 20\n" +
                                                "\tpro defend 25 : defend with 25\n" +
                                                "\tget, GET_INFO : request tower information\n" +
                                                "\tl, leave : to quit application";

    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use for the mulitcast connection (default: 1234).",
            defaultValue = "1234"
    )
    protected int port;

    @CommandLine.Option(
            names = {"-h", "--host"},
            description = "Host for the unicast connection",
            required = true,
            defaultValue = "172.19.0.4"
    )
    protected String host;

    public Client() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Integer call() {
        try {
            waitUserInput();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public void waitUserInput() throws RuntimeException {
        try (DatagramSocket socket = new DatagramSocket()) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            System.out.println("Client emitter started (" + myself + ")");
            InetAddress serverAddress = InetAddress.getByName(host);

            String message = "";
            MessageType messageType; // protect/pro or get_info/get
            int defaultValue = 10;

            String command;
            String[] commandSplit;
            String timestamp;
            //chaque fois qu'on send on attend la réponse sur LE MEME DATAGRAM envoyé on est donc seulement un emetteur
            while (true) {
                System.out.print("listen client console input [h for help] : ");
                command = sc.nextLine().toUpperCase(); // listen user input
                timestamp = dateFormat.format(new Date());
                commandSplit = command.split(" ");

                if (commandSplit.length == 0 || !MessageType.isIn(commandSplit[0])) {
                    if (    commandSplit[0].equalsIgnoreCase("HELP") ||
                            commandSplit[0].equalsIgnoreCase("H")) {
                        System.out.println(helpClient);
                        continue;
                    } else if ( commandSplit[0].equalsIgnoreCase("LEAVE") ||
                                commandSplit[0].equalsIgnoreCase("L")) {
                        System.out.println("Leaving...");
                        exit(0);
                    }
                }
                messageType = getByDimOrName(commandSplit[0]);

                if (messageType == null) {
                    System.out.println("[NOT A COMMAND] '" + command + "' is not recognized as a command. [enter 'h' for help]");
                    //message = MessageType.DEFAULT + " Hello, from '" + myself + "' not a command : <" + command + "> at " + timestamp + ")";
                    //send(message, timestamp, serverAddress, socket, host, port);
                    continue;
                }
                switch (messageType) {
                    case PROTECT:
                        if (commandSplit.length == 3) {
                            if (ProtectionType.isIn(commandSplit[1])) {
                                if (Utils.isNumeric(commandSplit[2])) {
                                    message = messageType.name() + " " + ProtectionType.getByDimOrName(commandSplit[1]) + " " + commandSplit[2];
                                } else {
                                    System.out.println("[NUMBER ERROR] '" + command + "' has a non numeric for the amount. [enter 'h' for help]");
                                    continue;
                                }
                            } else {
                                System.out.println("[PROTECTION TYPE NOT RECOGNIZED] '" + command + "' has a non-recognized protectionType. [enter 'h' for help]");
                                continue;
                            }
                        } else {
                            System.out.println("[PARAMETERS ERROR] '" + command + "' has a parameter length not recognize. [enter 'h' for help]");
                            continue;
                        }
                        break;

                    case GET_INFO:
                        message = messageType.name();
                        break;

                    default:
                        System.out.println("[MESSAGE TYPE NOT RECOGNIZED] '" + command + "' is not a recognized client command. [enter 'h' for help]");
                        continue;
                }

                send(message, timestamp, serverAddress, socket, host, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void send(String message, String timestamp, InetAddress serverAddress, DatagramSocket socket, String host, int port) throws Exception {
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
        System.out.println("data sent !");

        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

        socket.receive(responsePacket);

        message = new String(
                responsePacket.getData(),
                responsePacket.getOffset(),
                responsePacket.getLength(),
                StandardCharsets.UTF_8
        );
        MessageType messageType = MessageType.findByName(message.split(" ")[0]);
        //System.out.println("RECEIVED :" + message);
        if (messageType == null) {
            System.out.println("received message not handled : " + message);
            return;
        }
        //System.out.println("received : -" + message + "-"); // TODO : remove after tests (tmp debug)
        String[] splitMessage = message.split(" ");

        switch (messageType) {
            case ANSWER:
                if (splitMessage.length <= 1 || splitMessage[1].isEmpty()) {
                    System.out.println("[SERVER ERROR] : server give 0 info ?");
                } else {
                    System.out.print("[TOWER INFO] :");
                    for (int i = 1; i < splitMessage.length; ++i) {
                        System.out.print(" ");
                        System.out.print(splitMessage[i]);
                    }
                    System.out.println();
                }
                break;

            case GAME_LOST:
                System.out.println("[TOWER INFO] : GAME OVER X_X (press enter to leave)");
                sc.nextLine();
                exit(0);
                break;

            case ERROR:
                System.out.println("[SERVER ERROR] : invalid command :`" + message + "`");
                break;

            case PROTECT:
            case GET_INFO:
            case ATTACK:
            case DEFAULT:
                // ne devrait pas arriver
                break;
        }
    }
}
