package com.example.rompe1.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Puntuaciones.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla y columnas
    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_ID = "idScore";
    public static final String COLUMN_PLAYER = "Jugador";
    public static final String COLUMN_TIME = "Tiempo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla scores
        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLAYER + " TEXT, "
                + COLUMN_TIME + " TEXT)";
        db.execSQL(CREATE_SCORES_TABLE);
    }
    public boolean savePlayerScore(String playerName, String elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER, playerName);
        values.put(COLUMN_TIME, elapsedTime);

        long result = db.insert(TABLE_SCORES, null, values);
        db.close();
        return result != -1;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    // Método para añadir un nuevo score
    public void addScore(String player, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER, player);
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    // Método para obtener los scores y convertir el tiempo a segundos
    public ArrayList<Score> getAllScores() {
        ArrayList<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SCORES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String player = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER));
                @SuppressLint("Range") String timeString = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));

                // Convertir el tiempo a segundos
                int timeInSeconds = convertTimeToSeconds(timeString);

                scores.add(new Score(player, timeInSeconds));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Ordenar los scores de menor a mayor tiempo
        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score s1, Score s2) {
                return Integer.compare(s1.getTime(), s2.getTime());
            }
        });

        return scores;
    }

    // Método para convertir el tiempo de H:M:S a segundos
    private int convertTimeToSeconds(String timeString) {
        String[] timeParts = timeString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}