package ch.heigvd.client;

import ch.heigvd.utils.MessageType;
import ch.heigvd.utils.ProtectionType;
import ch.heigvd.utils.Utils;
import picocli.CommandLine;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static ch.heigvd.utils.MessageType.getByDimOrName;
import static ch.heigvd.utils.Utils.send;
import static java.lang.System.exit;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Tower Defense")
public class Client implements Callable<Integer> {
    private final Scanner sc = new Scanner(System.in);
    private final SimpleDateFormat dateFormat;

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

    public Integer waitUserInput() throws RuntimeException {
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
            while (true)
            {
                System.out.println("listen client console input :" + port);
                command = sc.nextLine().toUpperCase(); // listen user input
                timestamp = dateFormat.format(new Date());
                commandSplit = command.split(" ");

                if (commandSplit.length == 0 || !MessageType.isIn(commandSplit[0]))
                {
                    if (    commandSplit[0].equalsIgnoreCase("HELP") ||
                            commandSplit[0].equalsIgnoreCase("H"))
                    {
                        System.out.println("Help Command Menu :");
                        System.out.println("h, H, help, HELP : this menu");
                        System.out.println("pro heal 15 : heal with 15");
                        System.out.println("pro defend 15 : defend with 15");
                        System.out.println("get, GET_INFO : request tower information");
                        continue;
                    }
                    else if (   commandSplit[0].equalsIgnoreCase("LEAVE") ||
                                commandSplit[0].equalsIgnoreCase("L")) {
                        System.out.println("Leaving...");
                        exit(0);
                    }
                }
                messageType = getByDimOrName(commandSplit[0]);

                if (messageType == null)
                {
                    message = MessageType.DEFAULT + " Hello, from '" + myself + "' not a command : <" + command + "> at " + timestamp + ")";
                    send(message, timestamp, serverAddress, socket,host,port);
                    continue;
                }
                switch (messageType)
                {
                    case PROTECT :
                        if (commandSplit.length == 3 && ProtectionType.isIn(commandSplit[1]) && Utils.isNumeric(commandSplit[2]))
                        {
                            message = messageType.name() + " " + ProtectionType.getByDimOrName(commandSplit[1]) + " " + commandSplit[2];
                        }
                        else
                        {
                            message = messageType.name() + " " + ProtectionType.HEAL + " " + defaultValue;
                            System.out.println("Command error, send " + message + " instead of : " + command);
                        }
                        break;

                    case GET_INFO:
                        message = messageType.name();
                        break;

                    default:
                        message = MessageType.DEFAULT + " Hello, from '" + myself + "' error command : <" + command + "> at " + timestamp + ")";
                        break;

                }

                send(message, timestamp, serverAddress, socket,host,port);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
