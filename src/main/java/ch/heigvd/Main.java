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