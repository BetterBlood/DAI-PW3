package ch.heigvd;

import picocli.CommandLine;

import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;

public abstract class TowerDefense implements Callable<Integer> {
    @CommandLine.ParentCommand
    protected ch.heigvd.Main parent;

    protected SimpleDateFormat dateFormat;

    public TowerDefense() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
