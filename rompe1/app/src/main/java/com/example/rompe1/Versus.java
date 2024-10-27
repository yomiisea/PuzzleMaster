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
public class Versus extends AppCompatActivity {

    TextView tvA, tvX, tvC, tvD, tvE, tvF, tvG, tvH, tvI;
    TextView tvbA, tvbX, tvbC, tvbD, tvbE, tvbF, tvbG, tvbH, tvbI;
    TextView emptyTile,emptyTileb;
    Button btDesordena ;
    TextView tvTimer;
    List<String> solution;
    private int[][] PuzzleStateNum,PuzzleStateNumb;
    private Map<TextView, int[]> textViewPositions = new HashMap<>();
    private Map<TextView, int[]> textViewPositionsb = new HashMap<>();
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
        setContentView(R.layout.contra);

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

        tvbA = findViewById(R.id.tvbA);
        tvbX = findViewById(R.id.tvbX);
        tvbC = findViewById(R.id.tvbC);
        tvbD = findViewById(R.id.tvbD);
        tvbE = findViewById(R.id.tvbE);
        tvbF = findViewById(R.id.tvbF);
        tvbG = findViewById(R.id.tvbG);
        tvbH = findViewById(R.id.tvbH);
        tvbI = findViewById(R.id.tvbI);
        btDesordena = findViewById(R.id.btDesordena);


        tvTimer = findViewById(R.id.tvTimer);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        emptyTile = tvI;
        emptyTileb = tvbI;
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
        PuzzleStateNumb = new int[][]{
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

        textViewPositionsb.put(tvbA, new int[]{0, 0});
        textViewPositionsb.put(tvbX, new int[]{0, 1});
        textViewPositionsb.put(tvbC, new int[]{0, 2});
        textViewPositionsb.put(tvbD, new int[]{1, 0});
        textViewPositionsb.put(tvbE, new int[]{1, 1});
        textViewPositionsb.put(tvbF, new int[]{1, 2});
        textViewPositionsb.put(tvbG, new int[]{2, 0});
        textViewPositionsb.put(tvbH, new int[]{2, 1});
        textViewPositionsb.put(tvbI, new int[]{2, 2});

        updateTextViews();

        Arma();

        btDesordena.setOnClickListener(v -> {
            shufflePuzzle();
            resetTimer();
            startTimer();
        });


    }


    private void Arma() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTimerRunning) {
                    startTimer();
                }
                if (view instanceof TextView) {
                    TextView selectedTextView = (TextView) view;

                    // Verificar si el TextView pertenece al primer o al segundo tablero
                    if (textViewPositions.containsKey(selectedTextView)) {
                        moveTile(selectedTextView);  // Mover ficha del primer tablero
                    } else if (textViewPositionsb.containsKey(selectedTextView)) {
                        moveTileB(selectedTextView);  // Mover ficha del segundo tablero
                    }
                }
            }
        };

        // Asignar el listener para el primer tablero
        tvA.setOnClickListener(onClickListener);
        tvX.setOnClickListener(onClickListener);
        tvC.setOnClickListener(onClickListener);
        tvD.setOnClickListener(onClickListener);
        tvE.setOnClickListener(onClickListener);
        tvF.setOnClickListener(onClickListener);
        tvG.setOnClickListener(onClickListener);
        tvH.setOnClickListener(onClickListener);
        tvI.setOnClickListener(onClickListener);

        // Asignar el listener para el segundo tablero
        tvbA.setOnClickListener(onClickListener);
        tvbX.setOnClickListener(onClickListener);
        tvbC.setOnClickListener(onClickListener);
        tvbD.setOnClickListener(onClickListener);
        tvbE.setOnClickListener(onClickListener);
        tvbF.setOnClickListener(onClickListener);
        tvbG.setOnClickListener(onClickListener);
        tvbH.setOnClickListener(onClickListener);
        tvbI.setOnClickListener(onClickListener);
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

            if (checkWinCondition(PuzzleStateNum)) {
                stopTimer();
                if (checkWinCondition(PuzzleStateNum)) {
                    stopTimer();
                    showWinMessage("Jugador 1");
                }

            }
        }

        printPuzzleState(PuzzleStateNum);
    }

    private void moveTileB(TextView target) {
        int[] emptyPosB = getPositionB(emptyTileb);  // Corregido a getPositionB
        int[] targetPosB = getPositionB(target);  // Corregido a getPositionB

        if (isAdjacent(emptyPosB, targetPosB)) {
            // Intercambiar textos y colores
            String tempTextB = emptyTileb.getText().toString();
            emptyTileb.setText(target.getText());
            target.setText(tempTextB);

            int tempColorB = ((ColorDrawable) emptyTileb.getBackground()).getColor();
            emptyTileb.setBackgroundColor(((ColorDrawable) target.getBackground()).getColor());
            target.setBackgroundColor(tempColorB);

            // Actualizar el estado del segundo rompecabezas
            int tempB = PuzzleStateNumb[emptyPosB[0]][emptyPosB[1]];
            PuzzleStateNumb[emptyPosB[0]][emptyPosB[1]] = PuzzleStateNumb[targetPosB[0]][targetPosB[1]];
            PuzzleStateNumb[targetPosB[0]][targetPosB[1]] = tempB;

            emptyTileb = target;

            if (checkWinCondition(PuzzleStateNumb)) {
                stopTimer();
                if (checkWinCondition(PuzzleStateNumb)) {
                    stopTimer();
                    showWinMessage("Jugador 2");
                }

            }
        }

        printPuzzleState(PuzzleStateNumb);
    }



    private int[] getPosition(TextView textView) {
        return textViewPositions.get(textView);
    }
    private int[] getPositionB(TextView textView) {
        return textViewPositionsb.get(textView);
    }
    private boolean isAdjacent(int[] pos1, int[] pos2) {
        return (Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1])) == 1;
    }

    @SuppressLint("RestrictedApi")
    private void printPuzzleState(int[][] puzzleState) {
        for (int[] row : puzzleState) {
            Log.d(TAG, Arrays.toString(row));
        }
    }

    private boolean checkWinCondition(int[][] puzzleState) {
        int[][] winningState = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        return Arrays.deepEquals(puzzleState, winningState);
    }

    private void shufflePuzzle() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            values.add(i);  // Números del 0 al 8, donde 0 representa la casilla vacía
        }

        // Hacemos shuffle una vez para ambos rompecabezas
        do {
            Collections.shuffle(values);  // Barajar las piezas
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    PuzzleStateNum[i][j] = values.get(index);  // Aplicamos al primer rompecabezas
                    PuzzleStateNumb[i][j] = values.get(index); // Aplicamos al segundo rompecabezas
                    index++;
                }
            }
        } while (!isSolvable(PuzzleStateNum));  // Repetir hasta que sea resoluble

        updateTextViews();  // Actualiza los TextViews con los nuevos valores
        updateEmptyTile();  // Actualiza la referencia al TextView que está vacío

        // Intercambiar colores de los TextView
        shuffleColors();  // Nueva función que baraja los colores
    }

    private void shuffleColors() {
        List<Integer> colorsA = new ArrayList<>();
        List<Integer> colorsB = new ArrayList<>();

        // Obtener los colores actuales del primer rompecabezas
        colorsA.add(((ColorDrawable) tvA.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvX.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvC.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvD.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvE.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvF.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvG.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvH.getBackground()).getColor());
        colorsA.add(((ColorDrawable) tvI.getBackground()).getColor());

        // Obtener los colores actuales del segundo rompecabezas
        colorsB.add(((ColorDrawable) tvbA.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbX.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbC.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbD.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbE.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbF.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbG.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbH.getBackground()).getColor());
        colorsB.add(((ColorDrawable) tvbI.getBackground()).getColor());

        // Barajar los colores de ambos rompecabezas
        Collections.shuffle(colorsA);
        Collections.shuffle(colorsB);

        // Asignar los colores barajados a los TextViews del primer rompecabezas
        tvA.setBackgroundColor(colorsA.get(0));
        tvX.setBackgroundColor(colorsA.get(1));
        tvC.setBackgroundColor(colorsA.get(2));
        tvD.setBackgroundColor(colorsA.get(3));
        tvE.setBackgroundColor(colorsA.get(4));
        tvF.setBackgroundColor(colorsA.get(5));
        tvG.setBackgroundColor(colorsA.get(6));
        tvH.setBackgroundColor(colorsA.get(7));
        tvI.setBackgroundColor(colorsA.get(8));

        // Asignar los colores barajados a los TextViews del segundo rompecabezas
        tvbA.setBackgroundColor(colorsB.get(0));
        tvbX.setBackgroundColor(colorsB.get(1));
        tvbC.setBackgroundColor(colorsB.get(2));
        tvbD.setBackgroundColor(colorsB.get(3));
        tvbE.setBackgroundColor(colorsB.get(4));
        tvbF.setBackgroundColor(colorsB.get(5));
        tvbG.setBackgroundColor(colorsB.get(6));
        tvbH.setBackgroundColor(colorsB.get(7));
        tvbI.setBackgroundColor(colorsB.get(8));
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

        tvbA.setText(PuzzleStateNumb[0][0] == 0 ? "" : String.valueOf(PuzzleStateNumb[0][0]));
        tvbX.setText(PuzzleStateNumb[0][1] == 0 ? "" : String.valueOf(PuzzleStateNumb[0][1]));
        tvbC.setText(PuzzleStateNumb[0][2] == 0 ? "" : String.valueOf(PuzzleStateNumb[0][2]));
        tvbD.setText(PuzzleStateNumb[1][0] == 0 ? "" : String.valueOf(PuzzleStateNumb[1][0]));
        tvbE.setText(PuzzleStateNumb[1][1] == 0 ? "" : String.valueOf(PuzzleStateNumb[1][1]));
        tvbF.setText(PuzzleStateNumb[1][2] == 0 ? "" : String.valueOf(PuzzleStateNumb[1][2]));
        tvbG.setText(PuzzleStateNumb[2][0] == 0 ? "" : String.valueOf(PuzzleStateNumb[2][0]));
        tvbH.setText(PuzzleStateNumb[2][1] == 0 ? "" : String.valueOf(PuzzleStateNumb[2][1]));
        tvbI.setText(PuzzleStateNumb[2][2] == 0 ? "" : String.valueOf(PuzzleStateNumb[2][2]));
    }

    private void updateEmptyTile() {
        // Actualizar la casilla vacía para el primer rompecabezas
        for (Map.Entry<TextView, int[]> entry : textViewPositions.entrySet()) {
            if (PuzzleStateNum[entry.getValue()[0]][entry.getValue()[1]] == 0) {
                emptyTile = entry.getKey();
                break;
            }
        }

        // Actualizar la casilla vacía para el segundo rompecabezas
        for (Map.Entry<TextView, int[]> entry : textViewPositionsb.entrySet()) {
            if (PuzzleStateNumb[entry.getValue()[0]][entry.getValue()[1]] == 0) {
                emptyTileb = entry.getKey();
                break;
            }
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

    private void showWinMessage(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Versus.this);
        builder.setTitle("¡Victoria!")
                .setMessage(winner + " ha resuelto el rompecabezas primero.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
