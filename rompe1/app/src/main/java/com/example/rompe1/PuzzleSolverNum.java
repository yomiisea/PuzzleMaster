package com.example.rompe1;

import java.util.*;

public class PuzzleSolverNum {
    // Estado objetivo, este es el estado que representa la solución. Puedes adaptarlo a un tablero de textos.
    private static final int[][] GOAL_STATE = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};

    // Movimientos posibles: arriba, abajo, izquierda, derecha
    private static final int[] DX = {-1, 1, 0, 0}; // Movimientos en x
    private static final int[] DY = {0, 0, -1, 1}; // Movimientos en y
    private static final String[] MOVES = {"Up", "Down", "Left", "Right"};

    public static List<String> solve(int[][] startState) {
        PriorityQueue<PuzzleStateNum> openList = new PriorityQueue<>(Comparator.comparingInt(PuzzleStateNum::getPriority));
        Set<PuzzleStateNum> closedList = new HashSet<>();

        // Determinar la posición del espacio vacío (asumiendo que '0' representa la casilla vacía)
        int emptyX = 0, emptyY = 0;
        for (int i = 0; i < startState.length; i++) {
            for (int j = 0; j < startState[i].length; j++) {
                if (startState[i][j] == 0) {
                    emptyX = i;
                    emptyY = j;
                }
            }
        }

        // Crear el estado inicial
        PuzzleStateNum start = new PuzzleStateNum(startState, emptyX, emptyY, 0, calculateHeuristic(startState), null, null);
        openList.add(start);

        // Algoritmo de búsqueda A* para encontrar el camino
        while (!openList.isEmpty()) {
            PuzzleStateNum current = openList.poll();
            if (Arrays.deepEquals(current.board, GOAL_STATE)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            // Explorar los vecinos posibles
            for (int i = 0; i < 4; i++) {
                int newX = current.emptyX + DX[i];
                int newY = current.emptyY + DY[i];
                if (isValid(newX, newY)) {
                    // Crear un nuevo estado vecino al mover la casilla vacía
                    int[][] newBoard = copyBoard(current.board);
                    newBoard[current.emptyX][current.emptyY] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;

                    PuzzleStateNum neighbor = new PuzzleStateNum(newBoard, newX, newY, current.cost + 1, calculateHeuristic(newBoard), current, MOVES[i]);

                    if (closedList.contains(neighbor)) {
                        continue;
                    }

                    // Agregar a la lista abierta si no está ya presente, o actualizar si es un mejor camino
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    } else {
                        for (PuzzleStateNum existing : openList) {
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

        return null; // No se encontró una solución
    }

    // Calcula la heurística de Manhattan
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

    // Verifica si una posición es válida dentro del tablero
    private static boolean isValid(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    // Hace una copia del estado actual del tablero
    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return newBoard;
    }

    // Reconstruye el camino desde el estado final al inicial
    private static List<String> reconstructPath(PuzzleStateNum state) {
        List<String> path = new ArrayList<>();
        while (state.move != null) {
            path.add(state.move);
            state = state.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
