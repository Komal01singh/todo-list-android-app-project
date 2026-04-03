package com.application.todolistapp;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private List<String> subTasks;
    private int color; // Color for the card

    public Task(String title, int color) {
        this.title = title;
        this.color = color;
        this.subTasks = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getSubTasks() { return subTasks; }
    public void setSubTasks(List<String> subTasks) { this.subTasks = subTasks; }
    
    public void addSubTask(String subTask) {
        this.subTasks.add(subTask);
    }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
}
