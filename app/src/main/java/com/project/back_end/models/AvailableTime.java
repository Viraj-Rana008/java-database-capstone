package com.project.back_end.models;

import jakarta.persistence.*;
import java.time.LocalTime;

@Embeddable
public class AvailableTime {
    
    private LocalTime startTime;

    public AvailableTime() {
    }

    public AvailableTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
