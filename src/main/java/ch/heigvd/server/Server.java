package ch.heigvd.server;

import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "server", description = "Starts a server for a game of Tower Defense")
public class Server implements Callable<Integer> {


    public Integer call() {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // The number of threads in the pool must be the same as the number of tasks you want to run in parallel

        try {
            executorService.submit(this::alliesWorker); // Start the first task
            executorService.submit(this::ennemiesWorker); // Start the second task

            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Wait for termination
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            executorService.shutdown();
        }

        return 0;
    }

    public Integer alliesWorker() {
        // ...
        return 1;
    }

    public Integer ennemiesWorker() {
        // ...
        return 1;
    }
}