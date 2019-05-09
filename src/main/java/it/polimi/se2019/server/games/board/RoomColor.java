package it.polimi.se2019.server.games.board;

import it.polimi.se2019.server.games.Targetable;

public enum RoomColor implements Targetable {
    BLUE("BLUE"),
    PURPLE("PURPLE"),
    RED("RED"),
    WHITE("WHITE"),
    YELLOW("YELLOW");

    private String color;


    RoomColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}