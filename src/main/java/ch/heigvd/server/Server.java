package ch.heigvd.server;

import picocli.CommandLine;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "server", description = "Starts a server for a game of Tower Defense")
public class Server implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-i", "--interface"},
            description = "Interface to use",
            required = true
    )
    protected String interfaceName;

    @CommandLine.Option(
            names = {"-pu", "--portu"},
            description = "Port to use for the unicast connections (allies) (default: 1234).",
            defaultValue = "1234"
    )
    protected int portu;
    @CommandLine.Option(
            names = {"-pm", "--portm"},
            description = "Port to use for the mulitcast connection (ennemies) (default: 9876).",
            defaultValue = "9876"
    )
    protected int portm;

    @CommandLine.Option(
            names = {"-hu", "--hostu"},
            description = "IP address to use for the unicast connections",
            required = true
    )
    protected String hostu;

    @CommandLine.Option(
            names = {"-hm", "--hostm"},
            description = "Subnet range/multicast address to use.",
            required = true
    )
    protected String hostm;

    @CommandLine.Option(
            names = {"-bh", "--baseHP"},
            description = "Base hp for the tower.",
            defaultValue = "1000",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int baseHP;

    @CommandLine.Option(
            names = {"-bp", "--baseProtection"},
            description = "Base protection for the tower, between 0-100.",
            defaultValue = "1000",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int baseProtection;
    private TowerDefense tower;
    public Integer call() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        tower = new TowerDefense(baseHP,baseProtection);
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
        AlliesWorker a = new AlliesWorker(tower,portu,hostu);
        a.call();
        return 1;
    }

    public Integer ennemiesWorker() {
        EnnemiesWorker e = new EnnemiesWorker(tower,interfaceName,portm,hostm);
        e.call();
        return 1;
    }
}