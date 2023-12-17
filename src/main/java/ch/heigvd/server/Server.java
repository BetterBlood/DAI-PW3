package ch.heigvd.server;

import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "server", description = "Starts a server for a game of Tower Defense")
public class Server implements Callable<Integer> {

    //add port
    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;
    @CommandLine.Option(
            names = {"-i", "--interface"},
            description = "Interface to use",
            required = true
    )
    protected String interfaceName;

    @CommandLine.Option(
            names = {"-pu", "--portu"},
            description = "Port to use for the unicast connections (default: 1234).",
            defaultValue = "1234",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int portu; // enemies on 9876, allies on 1234
    @CommandLine.Option(
            names = {"-pm", "--portm"},
            description = "Port to use for the mulitcast connection (default: 9876).",
            defaultValue = "9876",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int portm;

    @CommandLine.Option(
            names = {"-hu", "--hostu"},
            description = "IP address to use for the unicast connections",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String hostu;

    @CommandLine.Option(
            names = {"-hm", "--hostm"},
            description = "Subnet range/multicast address to use.",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String hostm;
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
        AlliesWorker a = new AlliesWorker(tower,parent,portu,hostu);
        a.call();
        return 1;
    }

    public Integer ennemiesWorker() {
        EnnemiesWorker e = new EnnemiesWorker(tower,interfaceName,parent,portm,hostm);
        e.call();
        return 1;
    }
}