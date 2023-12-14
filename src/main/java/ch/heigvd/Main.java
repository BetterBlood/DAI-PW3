package ch.heigvd;

import ch.heigvd.client.Client;
import ch.heigvd.emitters.Enemy;
import ch.heigvd.server.Server;
import lombok.Getter;
import picocli.CommandLine;

@CommandLine.Command(
        description = "Tower Defense, third Practical Work of DAI",
        version = "1.0.0",
        subcommands = {
                Client.class,
                Enemy.class,
                Server.class
                // add other subcommands here

        },
        scope = CommandLine.ScopeType.INHERIT,
        mixinStandardHelpOptions = true
)
@Getter
public class Main {
    @CommandLine.Option(
            names = {"-p", "--port"},
            description = "Port to use (default: 5555).",
            defaultValue = "5555",
            scope = CommandLine.ScopeType.INHERIT
    )
    protected int port; // enemies on 9876, allies on 1234

    @CommandLine.Option(
            names = {"-h", "--host"},
            description = "Subnet range/multicast address to use.",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String host;

    @CommandLine.Option(
            names = {"-i", "--interface"},
            description = "Interface to use",
            required = true
    )
    protected String interfaceName;

    public static void main(String[] args) {
        // Source: https://stackoverflow.com/a/11159435
        String commandName = new java.io.File(
                Main.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
        ).getName();

        int exitCode = new CommandLine(new Main())
                .setCommandName(commandName)
                .execute(args);
        System.exit(exitCode);
    }
}