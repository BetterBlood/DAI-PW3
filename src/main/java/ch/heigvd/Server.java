package ch.heigvd;

import picocli.CommandLine;

@CommandLine.Command(name = "server",
        description = "Starts a server for a game of Tower Defense")
public class Server extends TowerDefense{

    @Override
    public Integer call() throws Exception {
        return null;
    }
}
