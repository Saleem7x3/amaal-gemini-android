package com.example.amaal.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "deeds")
public class Deed implements Comparable<Deed> {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public FiqhCategory category;
    public boolean isCompleted;
    
    public boolean isPrayer;
    public long scheduledTimeMillis;
    
    public long jamaahTimeMillis;
    public int priorityWindowMinutes;
    
    public int estimatedDuration; 

    public Deed() {
        // Required for Room
    }

    @Ignore
    public Deed(String title, FiqhCategory category, boolean isPrayer) {
        this.title = title;
        this.category = category;
        this.isPrayer = isPrayer;
        this.isCompleted = false;
        this.priorityWindowMinutes = 15;
        this.estimatedDuration = 5;
    }

    public long getPriorityScore() {
        if (isCompleted) return -1;

        long score = category.getWeight() * 1000L;
        long now = System.currentTimeMillis();

        if (isPrayer) {
            if (now < scheduledTimeMillis) {
                return 0; 
            }
            
            if (jamaahTimeMillis > 0) {
                long windowStart = jamaahTimeMillis - (priorityWindowMinutes * 60 * 1000L);
                if (now >= windowStart && now <= jamaahTimeMillis) {
                    score += 500;
                }
            }
            
            if (now >= scheduledTimeMillis) {
                score += 200;
            }
        }

        score -= estimatedDuration;

        return score;
    }

    @Override
    public int compareTo(Deed other) {
        return Long.compare(other.getPriorityScore(), this.getPriorityScore());
    }
}