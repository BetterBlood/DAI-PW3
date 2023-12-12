package ch.heigvd.client;

import ch.heigvd.utils.MessageType;
import ch.heigvd.utils.ProtectionType;
import ch.heigvd.utils.Utils;
import picocli.CommandLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.heigvd.utils.MessageType.getByDimOrName;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Tower Defense")
public class Client implements Callable<Integer> {
    private final Scanner sc = new Scanner(System.in);
    private final SimpleDateFormat dateFormat;

    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

    public Client() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Integer call() {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // The number of threads in the pool must be the same as the number of tasks you want to run in parallel

        try {
            executorService.submit(this::listenServer); // Start the first task
            executorService.submit(this::waitUserInput); // Start the second task

            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Wait for termination
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            executorService.shutdown();
        }
        return 0;
    }

    public Integer listenServer() {

        try (DatagramSocket socket = new DatagramSocket(parent.getPort())) {
            // This is new - the executor service has a pool of threads

            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + parent.getPort();
            System.out.println("Client receiver started (" + myself + ")");

            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(
                        receiveData,
                        receiveData.length
                );

                socket.receive(packet); // listen server
                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );
                MessageType messageType = MessageType.findByName(message.split(" ")[0]);
                if (messageType == null) {
                    System.out.println("received message not handled : " + message);
                    continue;
                }
                System.out.println("received :-" + message + "-"); // TODO : remove after tests (tmp debug)
                String[] splitMessage = message.split(" ");
                switch (messageType) // TODO : complete with interpretation of server answer
                {
                    case ANSWER :
                        if (splitMessage.length <= 1 || splitMessage[1].isEmpty())
                        {
                            System.out.println("server give 0 info ???");
                        }
                        else
                        {
                            System.out.println("[TOWER INFO] : " + splitMessage[1]);
                        }
                        break;

                    case ERROR_CD:
                        if (splitMessage.length <= 1 || splitMessage[1].isEmpty() || !Utils.isNumeric(splitMessage[1]))
                        {
                            System.out.println("[SERVER ERROR] : error_cd not provided correctly");
                        }
                        else
                        {
                            System.out.println("[ALLY] : time left before action authorized : " + splitMessage[1]);
                        }
                        break;

                    case GAME_LOST:
                        System.out.println("[TOWER INFO] : GAME OVER X_X");
                        exit(0); // TODO : quitter ou pas ?
                        break;

                    case ERROR:
                        System.out.println("[SERVER ERROR] : invalide commande :`" + splitMessage[0] + "`");
                        break;

                    case PROTECT:
                    case GET_INFO :
                        System.out.println("TODO : ignore, receive self information");
                        break;

                    case ATTACK:
                    case DEFAULT:
                        System.out.println("TODO : tmp test WIP, to ignore as PROTECT and GET_IMFO");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public Integer waitUserInput() throws RuntimeException {
        try (DatagramSocket socket = new DatagramSocket()) {
            String myself = InetAddress.getLocalHost().getHostAddress() + ":" + parent.getPort();
            System.out.println("Client emitter started (" + myself + ")");
            InetAddress serverAddress = InetAddress.getByName(parent.getHost());

            String message = "";
            MessageType messageType; // protect/pro or get_info/get
            int defaultValue = 10;

            String command;
            String[] commandSplit;
            String timestamp;

            while (true)
            {
                System.out.println("listen client console input :" + parent.getPort());
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
                    send(message, timestamp, serverAddress, socket);
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

                send(message, timestamp, serverAddress, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private void send(String message, String timestamp, InetAddress serverAddress, DatagramSocket socket) throws Exception
    {
        System.out.println("Unicasting '" + message + "' to " + parent.getHost() + ":" + parent.getPort() + " at " + timestamp);

        byte[] payload = message.getBytes(StandardCharsets.UTF_8);

        DatagramPacket datagram = new DatagramPacket(
                payload,
                payload.length,
                serverAddress,
                parent.getPort()
        );
        System.out.print("sending... ");
        socket.send(datagram); // send data to serv
        System.out.println("data sended !");

        sleep(200);// waiting for receiving an answer (not really gorgeous)
    }
}
