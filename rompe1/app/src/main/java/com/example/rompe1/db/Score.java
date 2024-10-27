package com.example.rompe1.db;

public class Score {
    private String player;
    private int time;

    public Score(String player, int time) {
        this.player = player;
        this.time = time;
    }

    public String getPlayer() {
        return player;
    }

    public int getTime() {
        return time;
    }
}