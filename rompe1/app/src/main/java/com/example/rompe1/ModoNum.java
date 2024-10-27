package com.example.rompe1;

import static androidx.fragment.app.FragmentManager.TAG;

import static java.util.Collections.swap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.PopupMenu;
import android.widget.ImageButton;
public class ModoNum extends AppCompatActivity {

    TextView tvA, tvX, tvC, tvD, tvE, tvF, tvG, tvH, tvI;
    TextView emptyTile;
    Button btDesordena, btOrdenar, btSube;
    TextView tvTimer;
    List<String> solution;
    private int[][] PuzzleStateNum;
    private Map<TextView, int[]> textViewPositions = new HashMap<>();
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0L;
    private boolean isTimerRunning = false;
    private long elapsedTime = 0L;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeros);
        ImageButton moreOptionsButton = findViewById(R.id.more_options_button);
        moreOptionsButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ModoNum.this, v);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.menu_option1) {
                    solution = PuzzleSolver.solve(PuzzleStateNum);
                    showNextMove(solution);
                    return true;
                }
                return false;
            });
            popup.show();
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Inicializar los TextViews
        tvA = findViewById(R.id.tvA);
        tvX = findViewById(R.id.tvX);
        tvC = findViewById(R.id.tvC);
        tvD = findViewById(R.id.tvD);
        tvE = findViewById(R.id.tvE);
        tvF = findViewById(R.id.tvF);
        tvG = findViewById(R.id.tvG);
        tvH = findViewById(R.id.tvH);
        tvI = findViewById(R.id.tvI);

        btDesordena = findViewById(R.id.btDesordena);
        btOrdenar = findViewById(R.id.btOrdenar);

        tvTimer = findViewById(R.id.tvTimer);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        emptyTile = tvI;

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(item -> {

            Intent intent = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                intent = new Intent(this, InicioActivity.class);
            } else if (id == R.id.nav_mode_numbers) {
                intent = new Intent(this, ModoNum.class);
            } else if (id == R.id.nav_mode_img) {
                intent = new Intent(this, MainActivity.class);
            }
            else if (id == R.id.nav_chart) {
                intent = new Intent(this, ScoreListActivity.class);
            }
            else if (id == R.id.nav_mode_versus) {
                intent = new Intent(this, Versus.class);
            }
            if (intent != null) {
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Estado inicial del rompecabezas con números
        PuzzleStateNum = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        textViewPositions.put(tvA, new int[]{0, 0});
        textViewPositions.put(tvX, new int[]{0, 1});
        textViewPositions.put(tvC, new int[]{0, 2});
        textViewPositions.put(tvD, new int[]{1, 0});
        textViewPositions.put(tvE, new int[]{1, 1});
        textViewPositions.put(tvF, new int[]{1, 2});
        textViewPositions.put(tvG, new int[]{2, 0});
        textViewPositions.put(tvH, new int[]{2, 1});
        textViewPositions.put(tvI, new int[]{2, 2});

        updateTextViews();

        Arma();

        btDesordena.setOnClickListener(v -> {
            shufflePuzzle();
            resetTimer();
            startTimer();
        });

        btOrdenar.setOnClickListener(v -> {
            printPuzzleState();
            solution = PuzzleSolverNum.solve(PuzzleStateNum);
            autoSolvePuzzle(solution);

            if (solution != null) {
                Log.d(TAG, "Solution found:");
                for (String move : solution) {
                    Log.d(TAG, move);
                }
            } else {
                Log.d(TAG, "No solution found.");
            }
        });
    }


    private void showNextMove(List<String> moves){
        String move = translateMove(moves.get(0)); // Traduce el primer movimiento
        showAlert("Te aconsejo realizar el siguiente movimiento: " + move);
    }
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Movimiento Sugerido")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    private String translateMove(String move) {
        switch (move) {
            case "Up":
                return "Arriba";
            case "Down":
                return "Abajo";
            case "Right":
                return "Derecha";
            case "Left":
                return "Izquierda";
            default:
                return move; // Devuelve el movimiento original si no se encuentra una traducción
        }
    }

    private void Arma() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTimerRunning) {
                    startTimer();
                }
                if (view instanceof TextView) {  // Asegúrate de que la vista sea un TextView
                    TextView selectedTextView = (TextView) view;
                    moveTile(selectedTextView);
                }
            }
        };


        tvA.setOnClickListener(onClickListener);
        tvX.setOnClickListener(onClickListener);
        tvC.setOnClickListener(onClickListener);
        tvD.setOnClickListener(onClickListener);
        tvE.setOnClickListener(onClickListener);
        tvF.setOnClickListener(onClickListener);
        tvG.setOnClickListener(onClickListener);
        tvH.setOnClickListener(onClickListener);
        tvI.setOnClickListener(onClickListener);
    }
    private void swap(int[][] puzzle, int x1, int y1, int x2, int y2) {
        int temp = puzzle[x1][y1];
        puzzle[x1][y1] = puzzle[x2][y2];
        puzzle[x2][y2] = temp;
    }

    private void moveTile(TextView target) {
        int[] emptyPos = getPosition(emptyTile);
        int[] targetPos = getPosition(target);

        if (isAdjacent(emptyPos, targetPos)) {
            // Intercambiar textos y colores
            String tempText = emptyTile.getText().toString();
            emptyTile.setText(target.getText());
            target.setText(tempText);

            int tempColor = ((ColorDrawable) emptyTile.getBackground()).getColor();
            emptyTile.setBackgroundColor(((ColorDrawable) target.getBackground()).getColor());
            target.setBackgroundColor(tempColor);

            // Actualizar el estado del rompecabezas
            int temp = PuzzleStateNum[emptyPos[0]][emptyPos[1]];
            PuzzleStateNum[emptyPos[0]][emptyPos[1]] = PuzzleStateNum[targetPos[0]][targetPos[1]];
            PuzzleStateNum[targetPos[0]][targetPos[1]] = temp;

            emptyTile = target;

            if (checkWinCondition()) {
                stopTimer();
                showWinMessage();
            }
        }

        printPuzzleState();
    }

    private int[] getPosition(TextView textView) {
        return textViewPositions.get(textView);
    }

    private boolean isAdjacent(int[] pos1, int[] pos2) {
        return (Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1])) == 1;
    }

    @SuppressLint("RestrictedApi")
    private void printPuzzleState() {
        for (int[] row : PuzzleStateNum) {
            Log.d(TAG, Arrays.toString(row));
        }
    }

    private boolean checkWinCondition() {
        int[][] winningState = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        return Arrays.deepEquals(PuzzleStateNum, winningState);
    }

    private void shufflePuzzle() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            values.add(i);  // Números del 0 al 8, donde 0 representa la casilla vacía
        }

        do {
            Collections.shuffle(values);  // Barajar las piezas

            // Pasar los valores al estado del rompecabezas
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    PuzzleStateNum[i][j] = values.get(index++);
                }
            }
        } while (!isSolvable(PuzzleStateNum));  // Repetir hasta que sea resoluble

        updateTextViews();  // Actualiza los TextViews con los nuevos valores
        updateEmptyTile();  // Actualiza la referencia al TextView que está vacío

        // Intercambiar colores de los TextView
        shuffleColors();  // Nueva función que baraja los colores
    }
    private void shuffleColors() {
        List<Integer> colors = new ArrayList<>();

        // Obtener los colores actuales de cada TextView
        colors.add(((ColorDrawable) tvA.getBackground()).getColor());
        colors.add(((ColorDrawable) tvX.getBackground()).getColor());
        colors.add(((ColorDrawable) tvC.getBackground()).getColor());
        colors.add(((ColorDrawable) tvD.getBackground()).getColor());
        colors.add(((ColorDrawable) tvE.getBackground()).getColor());
        colors.add(((ColorDrawable) tvF.getBackground()).getColor());
        colors.add(((ColorDrawable) tvG.getBackground()).getColor());
        colors.add(((ColorDrawable) tvH.getBackground()).getColor());
        colors.add(((ColorDrawable) tvI.getBackground()).getColor());

        // Barajar los colores
        Collections.shuffle(colors);

        // Asignar los colores barajados a los TextView
        tvA.setBackgroundColor(colors.get(0));
        tvX.setBackgroundColor(colors.get(1));
        tvC.setBackgroundColor(colors.get(2));
        tvD.setBackgroundColor(colors.get(3));
        tvE.setBackgroundColor(colors.get(4));
        tvF.setBackgroundColor(colors.get(5));
        tvG.setBackgroundColor(colors.get(6));
        tvH.setBackgroundColor(colors.get(7));
        tvI.setBackgroundColor(colors.get(8));
    }

    private boolean isSolvable(int[][] puzzle) {
        int[] flatPuzzle = new int[9];
        int index = 0;

        // Convertir la matriz 2D en un array 1D
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                flatPuzzle[index++] = puzzle[i][j];
            }
        }

        // Contar el número de inversiones
        int inversions = 0;
        for (int i = 0; i < flatPuzzle.length - 1; i++) {
            for (int j = i + 1; j < flatPuzzle.length; j++) {
                if (flatPuzzle[i] != 0 && flatPuzzle[j] != 0 && flatPuzzle[i] > flatPuzzle[j]) {
                    inversions++;
                }
            }
        }

        // Si el número de inversiones es par, el puzzle es resoluble
        return inversions % 2 == 0;
    }

    private void updateTextViews() {
        // Actualiza los TextViews con los valores de puzzleState
        tvA.setText(PuzzleStateNum[0][0] == 0 ? "" : String.valueOf(PuzzleStateNum[0][0]));
        tvX.setText(PuzzleStateNum[0][1] == 0 ? "" : String.valueOf(PuzzleStateNum[0][1]));
        tvC.setText(PuzzleStateNum[0][2] == 0 ? "" : String.valueOf(PuzzleStateNum[0][2]));
        tvD.setText(PuzzleStateNum[1][0] == 0 ? "" : String.valueOf(PuzzleStateNum[1][0]));
        tvE.setText(PuzzleStateNum[1][1] == 0 ? "" : String.valueOf(PuzzleStateNum[1][1]));
        tvF.setText(PuzzleStateNum[1][2] == 0 ? "" : String.valueOf(PuzzleStateNum[1][2]));
        tvG.setText(PuzzleStateNum[2][0] == 0 ? "" : String.valueOf(PuzzleStateNum[2][0]));
        tvH.setText(PuzzleStateNum[2][1] == 0 ? "" : String.valueOf(PuzzleStateNum[2][1]));
        tvI.setText(PuzzleStateNum[2][2] == 0 ? "" : String.valueOf(PuzzleStateNum[2][2]));
    }

    private void updateEmptyTile() {
        // Busca la casilla vacía y actualiza la referencia de emptyTile
        for (Map.Entry<TextView, int[]> entry : textViewPositions.entrySet()) {
            int[] position = entry.getValue();
            if (PuzzleStateNum[position[0]][position[1]] == 0) {
                emptyTile = entry.getKey();
                return;
            }
        }
    }

    private void showWinMessageAuto() {
        new AlertDialog.Builder(this)
                .setTitle("¡ARMADO!")
                .setMessage("¡El Rompecabezas esta completo")
                .setPositiveButton("OK", null)
                .show();
    }
    private void autoSolvePuzzle(List<String> moves) {
        Handler handler = new Handler();
        int delay = 400; // 400ms entre movimientos

        // Crear un Runnable que se ejecutará después de que todos los movimientos se hayan completado
        Runnable onComplete = new Runnable() {
            @Override
            public void run() {
                showWinMessageAuto();
                stopTimer();
            }
        };

        for (int i = 0; i < moves.size(); i++) {
            final int index = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Ejecutar el movimiento en el rompecabezas
                    executeMove(moves.get(index));

                    // Actualizar la interfaz después de cada movimiento
                    updatePuzzleTextViews();

                    // Si es el último movimiento, ejecutar el Runnable onComplete
                    if (index == moves.size() - 1) {
                        handler.postDelayed(onComplete, delay); // Asegúrate de añadir el delay final
                    }
                }
            }, i * delay);
        }
    }

    private void executeMove(String move) {
        int emptyX = -1, emptyY = -1;

        // Encontrar el espacio vacío
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (PuzzleStateNum[i][j] == 0) {
                    emptyX = i;
                    emptyY = j;
                    break;
                }
            }
        }

        // Realizar el movimiento según la dirección
        switch (move) {
            case "Up":
                if (emptyX > 0) {
                    swap(PuzzleStateNum, emptyX, emptyY, emptyX - 1, emptyY);
                }
                break;
            case "Down":
                if (emptyX < 2) {
                    swap(PuzzleStateNum, emptyX, emptyY, emptyX + 1, emptyY);
                }
                break;
            case "Left":
                if (emptyY > 0) {
                    swap(PuzzleStateNum, emptyX, emptyY, emptyX, emptyY - 1);
                }
                break;
            case "Right":
                if (emptyY < 2) {
                    swap(PuzzleStateNum, emptyX, emptyY, emptyX, emptyY + 1);
                }
                break;
        }
    }


    private void updatePuzzleTextViews() {
        // Actualiza los TextViews con los valores de puzzleState
        updateTextView(tvA, PuzzleStateNum[0][0]);
        updateTextView(tvX, PuzzleStateNum[0][1]);
        updateTextView(tvC, PuzzleStateNum[0][2]);
        updateTextView(tvD, PuzzleStateNum[1][0]);
        updateTextView(tvE, PuzzleStateNum[1][1]);
        updateTextView(tvF, PuzzleStateNum[1][2]);
        updateTextView(tvG, PuzzleStateNum[2][0]);
        updateTextView(tvH, PuzzleStateNum[2][1]);
        updateTextView(tvI, PuzzleStateNum[2][2]);
    }

    private void updateTextView(TextView textView, int value) {
        if (value == 0) {
            textView.setText(""); // Casilla vacía
        } else {
            textView.setText(String.valueOf(value));
        }
    }




    private void startTimer() {
        startTime = System.currentTimeMillis();
        isTimerRunning = true;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tvTimer.setText(String.format("%d:%02d", minutes, seconds));

                if (isTimerRunning) {
                    timerHandler.postDelayed(this, 500);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        stopTimer();
        elapsedTime = 0L;
        tvTimer.setText("0:00");
    }

    private void showWinMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Felicidades!");
        builder.setMessage("Has resuelto el rompecabezas.");
        builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
