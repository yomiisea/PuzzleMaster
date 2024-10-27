package com.example.rompe1;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.rompe1.db.DatabaseHelper;
import com.google.android.filament.BuildConfig;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.ImageButton;
import android.widget.PopupMenu;
public class MainActivity extends AppCompatActivity {
    DatabaseHelper db;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int CAMERA_REQUEST_CODE = 100;

    int[][] puzzleGoal = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };
    ImageView ivA, ivX, ivC, ivD, ivE, ivF, ivG, ivH, ivI;
    ImageView emptyTile;

    private String currentPhotoPath;

    Button btDesordena, btOrdenar,btSube;
    TextView tvTimer;
    Bitmap[] imageParts;
    List<String> solution;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private int[][] puzzleState;
    private Map<ImageView, int[]> imageViewPositions = new HashMap<>();
    private Uri imageUri;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        db = new DatabaseHelper(this);
        setSupportActionBar(toolbar);
        ImageButton moreOptionsButton = findViewById(R.id.more_options_button);
        moreOptionsButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.menu_option1) {
                    solution = PuzzleSolver.solve(puzzleState);
                    showNextMove(solution);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        getSupportActionBar().setTitle("");
        ivA = findViewById(R.id.ivA);
        ivX = findViewById(R.id.ivX);
        ivC = findViewById(R.id.ivC);
        ivD = findViewById(R.id.ivD);
        ivE = findViewById(R.id.ivE);
        ivF = findViewById(R.id.ivF);
        ivG = findViewById(R.id.ivG);
        ivH = findViewById(R.id.ivH);
        ivI = findViewById(R.id.ivI);
        btDesordena = findViewById(R.id.btDesordena);
        btOrdenar = findViewById(R.id.btOrdenar);
        btSube = findViewById(R.id.btSube);
        tvTimer = findViewById(R.id.tvTimer);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        emptyTile = ivI;
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        // Cargar la imagen y dividirla
        Bitmap fullImage = BitmapFactory.decodeResource(getResources(), R.drawable.full_image);
        imageParts = splitImagen(fullImage, 3, 3);

        ivA.setImageBitmap(imageParts[0]);
        ivX.setImageBitmap(imageParts[1]);
        ivC.setImageBitmap(imageParts[2]);
        ivD.setImageBitmap(imageParts[3]);
        ivE.setImageBitmap(imageParts[4]);
        ivF.setImageBitmap(imageParts[5]);
        ivG.setImageBitmap(imageParts[6]);
        ivH.setImageBitmap(imageParts[7]);
        ivI.setImageBitmap(imageParts[8]);


        puzzleState = new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        imageViewPositions.put(ivA, new int[]{0, 0});
        imageViewPositions.put(ivX, new int[]{0, 1});
        imageViewPositions.put(ivC, new int[]{0, 2});
        imageViewPositions.put(ivD, new int[]{1, 0});
        imageViewPositions.put(ivE, new int[]{1, 1});
        imageViewPositions.put(ivF, new int[]{1, 2});
        imageViewPositions.put(ivG, new int[]{2, 0});
        imageViewPositions.put(ivH, new int[]{2, 1});
        imageViewPositions.put(ivI, new int[]{2, 2});

        Arma();

        btDesordena.setOnClickListener(v -> {
            shufflePuzzle();
            resetTimer();
            startTimer();
        });
        btOrdenar.setOnClickListener(v -> {
            //ordenarPuzzle();
            printPuzzleState();
            //abrirCamara();

            solution = PuzzleSolver.solve(puzzleState);
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

        btSube.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Seleccionar Imagen")
                    .setItems(new CharSequence[]{"Galería", "Cámara"}, (dialog, which) -> {
                        if (which == 0) {
                            // Opción para abrir la galería
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);
                        } else if (which == 1) {
                            // Opción para abrir la cámara
                            checkCameraPermission();
                        }
                    })
                    .show();
        });



    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,2);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crear un nombre de archivo único
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guardar la ruta del archivo para usarla después
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Aquí es donde va el método onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso fue concedido, se abre la cámara
                dispatchTakePictureIntent();
            } else {
                // Si el permiso fue denegado
                Toast.makeText(this, "Se necesita el permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            // Permiso ya otorgado, abrir la cámara
            abrirCamara();
        }
    }


    private void Arma() {
        View.OnClickListener onClickListener = view -> {
            if (!isTimerRunning) {
                startTimer();
            }
            ImageView selectedImageView = (ImageView) view;
            moveTile(selectedImageView);
        };

        ivA.setOnClickListener(onClickListener);
        ivX.setOnClickListener(onClickListener);
        ivC.setOnClickListener(onClickListener);
        ivD.setOnClickListener(onClickListener);
        ivE.setOnClickListener(onClickListener);
        ivF.setOnClickListener(onClickListener);
        ivG.setOnClickListener(onClickListener);
        ivH.setOnClickListener(onClickListener);
        ivI.setOnClickListener(onClickListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // Imagen seleccionada desde la galería
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    processImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                // Imagen tomada desde la cámara
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                processImage(imageBitmap);
            }
        }
    }


    private void processImage(Bitmap bitmap) {
        // Dividir la imagen en partes
        imageParts = splitImagen(bitmap, 3, 3);

        // Actualizar las imágenes en los ImageView
        updatePuzzleImages();

        // Opcional: Designar el cuadro vacío si es necesario
        emptyTile = ivI; // O el ImageView que quieras designar como vacío
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
    private void moveTile(ImageView target) {
        int[] emptyPos = getPosition(emptyTile);
        int[] targetPos = getPosition(target);

        if (isAdjacent(emptyPos, targetPos)) {
            // Intercambiar imágenes en la vista
            swapImageViews(emptyTile, target);

            // Actualizar la matriz
            int temp = puzzleState[emptyPos[0]][emptyPos[1]];
            puzzleState[emptyPos[0]][emptyPos[1]] = puzzleState[targetPos[0]][targetPos[1]];
            puzzleState[targetPos[0]][targetPos[1]] = temp;

            // Actualizar el espacio vacío
            emptyTile = target;

            if (checkWinCondition()) {
                stopTimer();
                showWinMessage();
            }
        }
        printPuzzleState();
    }





    private int[] getPosition(ImageView imageView) {
        return imageViewPositions.get(imageView);
    }
    private ImageView getImageView(int[] position) {
        for (Map.Entry<ImageView, int[]> entry : imageViewPositions.entrySet()) {
            if (Arrays.equals(entry.getValue(), position)) {
                return entry.getKey();
            }
        }
        return null; // o lanzar una excepción si no se encuentra
    }
    private void printPuzzleState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < puzzleState.length; i++) {
            for (int j = 0; j < puzzleState[i].length; j++) {
                sb.append(puzzleState[i][j]).append(" ");
            }
            sb.append("\n");
        }
        Log.d("PuzzleState", sb.toString());
    }


    public boolean checkWinCondition() {
        // Comprobar que ambas matrices son iguales
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (puzzleState[i][j] != puzzleGoal[i][j]) {
                    return false;  // Si hay un solo elemento diferente, retorna false
                }
            }
        }

        return true;  // Si todos los elementos son iguales, retorna true
    }

    private boolean areBitmapsEqual(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1.getWidth() != bmp2.getWidth() || bmp1.getHeight() != bmp2.getHeight()) {
            return false;
        }

        for (int x = 0; x < bmp1.getWidth(); x++) {
            for (int y = 0; y < bmp1.getHeight(); y++) {
                if (bmp1.getPixel(x, y) != bmp2.getPixel(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void showWinMessage() {
        // Crear un EditText para ingresar el nombre del jugador
        final EditText input = new EditText(this);
        input.setHint("Ingrese su nombre");

        // Crear el cuadro de diálogo
        new AlertDialog.Builder(this)
                .setTitle("¡Ganaste!")
                .setMessage("¡Felicidades, has armado el rompecabezas en " + getFormattedTime(elapsedTime) + "!")
                .setView(input) // Añadir el EditText al cuadro de diálogo
                .setPositiveButton("OK", (dialog, which) -> {
                    String playerName = input.getText().toString();
                    boolean isInserted = db.savePlayerScore(playerName, getFormattedTime(elapsedTime));
                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Puntuación añadida", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al añadir puntuación", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
        int delay = 400; // 500ms entre movimientos

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
                    updatePuzzleImages();

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
                if (puzzleState[i][j] == 0) {
                    emptyX = i;
                    emptyY = j;
                    break;
                }
            }
        }

        // Realizar el movimiento según la dirección
        switch (move) {
            case "Up":
                swap(puzzleState,emptyX,emptyY,emptyX-1,emptyY);
                break;
            case "Down":
                swap(puzzleState,emptyX,emptyY,emptyX+1,emptyY);
                break;
            case "Left":
                swap(puzzleState,emptyX,emptyY,emptyX,emptyY-1);
                break;
            case "Right":
                swap(puzzleState,emptyX,emptyY,emptyX,emptyY+1);
                break;
        }
    }
    private void swap(int[][] matrix, int x1, int y1, int x2, int y2) {
        int temp = matrix[x1][y1];
        matrix[x1][y1] = matrix[x2][y2];
        matrix[x2][y2] = temp;
    }
    private boolean isAdjacent(int[] pos1, int[] pos2) {
        int rowDiff = Math.abs(pos1[0] - pos2[0]);
        int colDiff = Math.abs(pos1[1] - pos2[1]);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    private void swapImageViews(ImageView tile1, ImageView tile2) {
        Drawable tempDrawable = tile1.getDrawable();
        tile1.setImageDrawable(tile2.getDrawable());
        tile2.setImageDrawable(tempDrawable);
    }

    private void shufflePuzzle() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            values.add(i);
        }

        do {
            Collections.shuffle(values);  // Barajar las piezas

            // Pasar los valores al estado del rompecabezas
            int index = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    puzzleState[i][j] = values.get(index++);
                }
            }
        } while (!isSolvable(puzzleState));  // Repetir hasta que sea resoluble

        updatePuzzleImages();
        updateEmptyTile();
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
    private void updatePuzzleImages() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = puzzleState[i][j];
                ImageView imageView = getImageViewAtPosition(i, j);
                if (value == 0) {
                    imageView.setImageDrawable(null); // Vacío
                } else {
                    imageView.setImageBitmap(imageParts[value - 1]); // Ajusta el índice si es necesario
                }
            }
        }
    }
    private void updateEmptyTile() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (puzzleState[i][j] == 0) {
                    emptyTile = getImageViewAtPosition(i, j);
                    return;
                }
            }
        }
    }

    private ImageView getImageViewAtPosition(int row, int col) {
        switch (row) {
            case 0:
                switch (col) {
                    case 0: return ivA;
                    case 1: return ivX;
                    case 2: return ivC;
                }
            case 1:
                switch (col) {
                    case 0: return ivD;
                    case 1: return ivE;
                    case 2: return ivF;
                }
            case 2:
                switch (col) {
                    case 0: return ivG;
                    case 1: return ivH;
                    case 2: return ivI;
                }
        }
        return null; // En caso de error
    }
    private Bitmap[] splitImagen(Bitmap image, int rows, int cols) {
        int partWidth = image.getWidth() / cols;
        int partHeight = image.getHeight() / rows;

        Bitmap[] imageParts = new Bitmap[rows * cols];
        int k = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                imageParts[k] = Bitmap.createBitmap(image, j * partWidth, i * partHeight, partWidth, partHeight);
                k++;
            }
        }

        return imageParts;
    }


    private void startTimer() {
        startTime = System.currentTimeMillis();
        isTimerRunning = true;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                tvTimer.setText(getFormattedTime(elapsedTime));
                timerHandler.postDelayed(this, 1000); // Actualiza cada segundo
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (isTimerRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
        }
    }

    private void resetTimer() {
        elapsedTime = 0L;
        tvTimer.setText(getFormattedTime(elapsedTime));
    }

    private String getFormattedTime(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
