package ch.heigvd;

import lombok.Getter;
import picocli.CommandLine;

@CommandLine.Command(
        description = "Tower Defense, third Practical Work of DAI",
        version = "1.0.0",
        subcommands = {
                // add subcommands here
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