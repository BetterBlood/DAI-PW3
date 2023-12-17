package ch.heigvd.server;

import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "server", description = "Starts a server for a game of Tower Defense")
public class Server implements Callable<Integer> {

    //add port

    private TowerDefense tower;
    public Integer call() {
        ExecutorService executorService = Executors.newFixedThreadPool(2); // The number of threads in the pool must be the same as the number of tasks you want to run in parallel
        tower = new TowerDefense(1000,1000);
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
        AlliesWorker a = new AlliesWorker(tower);
        a.call();
        return 1;
    }

    public Integer ennemiesWorker() {
        EnnemiesWorker e = new EnnemiesWorker(tower);
        e.call();
        return 1;
    }
}