package ch.heigvd;

import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class TowerDefense implements Callable<Integer> {
    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;
}
