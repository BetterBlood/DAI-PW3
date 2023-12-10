package ch.heigvd;

import picocli.CommandLine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@CommandLine.Command(name = "client", description = "Starts a client for a game of Tower Defense")
public class Client extends TowerDefense {

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
        try
        {
            sleep(2500);

            while(true)
            {
                // listen server
                // display info in console
                System.out.println("listen serv " + parent.port);
                Thread.sleep (5000);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error : " + e.getMessage());
        }
        return 0;
    }

    public Integer waitUserInput() {
        try
        {
            while(true)
            {
                // listen user input
                // send data to serv
                System.out.println("listen client console input " + parent.port);
                Thread.sleep (5000);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error : " + e.getMessage());
        }
        return 0;
    }
}
