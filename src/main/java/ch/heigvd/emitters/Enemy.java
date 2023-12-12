package ch.heigvd.emitters;

import picocli.CommandLine;

import java.util.concurrent.Callable;

abstract public class Enemy implements Callable<Integer> {
    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

    @CommandLine.Option(
            names = {"-H", "--host"},
            description = "Subnet range/multicast address to use.",
            required = true,
            scope = CommandLine.ScopeType.INHERIT
    )
    protected String host;

    @CommandLine.Option(
            names = {"-p", "--pause"},
            description = "Pause between attacks (in milliseconds) (default: 10000).",
            defaultValue = "10000"
    )
    protected int pause;

    @CommandLine.Option(
            names = {"-d", "--damage"},
            description = "Frequency of sending the message (in milliseconds) (default: 10000).",
            defaultValue = "10000"
    )
    protected int damage;

}
