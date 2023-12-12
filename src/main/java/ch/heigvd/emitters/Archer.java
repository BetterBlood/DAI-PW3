package ch.heigvd.emitters;

import picocli.CommandLine;

@CommandLine.Command(
        name = "archer",
        description = "Start an UDP multicast emitter representing an archer"
)
public class Archer extends Enemy { }
