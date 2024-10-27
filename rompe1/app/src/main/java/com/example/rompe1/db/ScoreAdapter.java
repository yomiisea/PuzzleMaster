package com.example.rompe1.db;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rompe1.R;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<Score> scoreList;

    public ScoreAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playerTextView;
        public TextView timeTextView;

        public ViewHolder(View view) {
            super(view);
            playerTextView = view.findViewById(R.id.player_name);
            timeTextView = view.findViewById(R.id.player_time);
        }
    }

    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.playerTextView.setText(score.getPlayer());
        holder.timeTextView.setText(formatTime(score.getTime()));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}