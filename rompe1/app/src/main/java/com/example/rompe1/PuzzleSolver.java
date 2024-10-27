package com.example.rompe1;

// PuzzleSolver.java
import java.util.*;

public class PuzzleSolver {
    private static final int[][] GOAL_STATE = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private static final int[] DX = {-1, 1, 0, 0}; // Movimientos en x
    private static final int[] DY = {0, 0, -1, 1}; // Movimientos en y
    private static final String[] MOVES = {"Up", "Down", "Left", "Right"};

    public static List<String> solve(int[][] startState) {
        PriorityQueue<PuzzleState> openList = new PriorityQueue<>(Comparator.comparingInt(PuzzleState::getPriority));
        Set<PuzzleState> closedList = new HashSet<>();

        int emptyX = 0, emptyY = 0;
        for (int i = 0; i < startState.length; i++) {
            for (int j = 0; j < startState[i].length; j++) {
                if (startState[i][j] == 0) {
                    emptyX = i;
                    emptyY = j;
                }
            }
        }

        PuzzleState start = new PuzzleState(startState, emptyX, emptyY, 0, calculateHeuristic(startState), null, null);
        openList.add(start);

        while (!openList.isEmpty()) {
            PuzzleState current = openList.poll();
            if (Arrays.deepEquals(current.board, GOAL_STATE)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int i = 0; i < 4; i++) {
                int newX = current.emptyX + DX[i];
                int newY = current.emptyY + DY[i];
                if (isValid(newX, newY)) {
                    int[][] newBoard = copyBoard(current.board);
                    newBoard[current.emptyX][current.emptyY] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;
                    PuzzleState neighbor = new PuzzleState(newBoard, newX, newY, current.cost + 1, calculateHeuristic(newBoard), current, MOVES[i]);

                    if (closedList.contains(neighbor)) {
                        continue;
                    }

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    } else {
                        for (PuzzleState existing : openList) {
                            if (existing.equals(neighbor) && existing.getPriority() > neighbor.getPriority()) {
                                openList.remove(existing);
                                openList.add(neighbor);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return null; // No solution found
    }

    // Modifica la función de cálculo de la heurística
    private static int calculateHeuristic(int[][] board) {
        int manhattanDistance = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int value = board[i][j];
                if (value != 0) { // No contar el espacio vacío
                    int targetX = (value - 1) / 3; // Fila objetivo
                    int targetY = (value - 1) % 3; // Columna objetivo
                    manhattanDistance += Math.abs(i - targetX) + Math.abs(j - targetY);
                }
            }
        }

        return manhattanDistance;
    }
    private static boolean isValid(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return newBoard;
    }

    private static List<String> reconstructPath(PuzzleState state) {
        List<String> path = new ArrayList<>();
        while (state.move != null) {
            path.add(state.move);
            state = state.parent;
        }
        Collections.reverse(path);
        return path;
    }
}