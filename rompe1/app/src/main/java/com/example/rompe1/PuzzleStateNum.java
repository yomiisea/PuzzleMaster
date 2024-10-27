package com.example.rompe1;// PuzzleState.java
import java.util.Arrays;

class PuzzleStateNum {
    int[][] board; // La matriz sigue representando el estado de los textos o números en el rompecabezas
    int emptyX, emptyY; // Posición de la casilla vacía
    int cost; // Costo del estado
    int heuristic; // Heurística del estado (para el algoritmo de búsqueda)
    PuzzleStateNum parent; // Estado padre en la búsqueda
    String move; // Movimiento que llevó a este estado

    public PuzzleStateNum(int[][] board, int emptyX, int emptyY, int cost, int heuristic, PuzzleStateNum parent, String move) {
        this.board = board;
        this.emptyX = emptyX;
        this.emptyY = emptyY;
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.move = move;
    }

    public int getPriority() {
        return cost + heuristic; // Para algoritmos de búsqueda (como A*)
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleStateNum that = (PuzzleStateNum) obj;
        return Arrays.deepEquals(board, that.board); // Comparar los estados del tablero
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board); // Para utilizar en estructuras de datos como HashSet
    }
}
