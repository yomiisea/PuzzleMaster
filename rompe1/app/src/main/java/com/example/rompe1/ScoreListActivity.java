package com.example.rompe1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rompe1.db.DatabaseHelper;
import com.example.rompe1.db.Score;
import com.example.rompe1.db.ScoreAdapter;

import java.util.List;

public class ScoreListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        List<Score> scoreList = db.getAllScores();

        adapter = new ScoreAdapter(scoreList);
        recyclerView.setAdapter(adapter);
    }
}