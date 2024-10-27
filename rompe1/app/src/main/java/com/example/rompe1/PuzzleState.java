package com.example.rompe1;

// PuzzleState.java
import java.util.Arrays;

class PuzzleState {
    int[][] board;
    int emptyX, emptyY;
    int cost;
    int heuristic;
    PuzzleState parent;
    String move;

    public PuzzleState(int[][] board, int emptyX, int emptyY, int cost, int heuristic, PuzzleState parent, String move) {
        this.board = board;
        this.emptyX = emptyX;
        this.emptyY = emptyY;
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.move = move;
    }

    public int getPriority() {
        return cost + heuristic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleState that = (PuzzleState) obj;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}