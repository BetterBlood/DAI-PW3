package ch.heigvd.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    public class AlliesWorker implements Callable<Integer> {
    protected ch.heigvd.Main parent;
        // This is new - could be passed as a parameter with picocli
        private static final int NUMBER_OF_THREADS = 1;

        @Override
        public Integer call() {
            // This is new - we define an executor service
            ExecutorService executor = null;

            try (DatagramSocket socket = new DatagramSocket(parent.getPort())) {
                // This is new - the executor service has a pool of threads
                executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

                String myself = InetAddress.getLocalHost().getHostAddress() + ":" + parent.getPort();
                System.out.println("Unicast receiver started (" + myself + ")");

                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(
                            receiveData,
                            receiveData.length
                    );

                    socket.receive(packet);

                    // This is new - we submit a new task to the executor service
                    executor.submit(new ClientHandler(packet, myself));
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

            public ClientHandler(DatagramPacket packet, String myself) {
                this.packet = packet;
                this.myself = myself;
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

                System.out.println("Going to sleep for 10 seconds...");

                // Sleep for a while to simulate a long-running task
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("End of sleep");
            }
        }
    }
