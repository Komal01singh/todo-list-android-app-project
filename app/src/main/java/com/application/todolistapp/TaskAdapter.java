package com.application.todolistapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<Task> taskList;
    private final OnTaskActionListener actionListener;

    public interface OnTaskActionListener {
        void onDeleteTask(int position);
        void onDataChanged();
    }

    public TaskAdapter(ArrayList<Task> taskList, OnTaskActionListener actionListener) {
        this.taskList = taskList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textTask.setText(task.getTitle());
        
        // Apply Color
        holder.cardView.setCardBackgroundColor(task.getColor());
        
        // Pick an icon based on title or just a default colorful one
        holder.imageTask.setImageResource(android.R.drawable.ic_menu_today); // Default icon
        
        holder.btnDelete.setOnClickListener(v -> actionListener.onDeleteTask(position));
        holder.btnAddSubTask.setOnClickListener(v -> showAddSubTaskDialog(holder.itemView.getContext(), task, position));

        // Render sub-tasks
        holder.subTaskContainer.removeAllViews();
        for (int i = 0; i < task.getSubTasks().size(); i++) {
            final int subIndex = i;
            String subTitle = task.getSubTasks().get(i);
            
            View subView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_subtask, holder.subTaskContainer, false);
            
            TextView textSub = subView.findViewById(R.id.textSubTask);
            ImageButton btnDelSub = subView.findViewById(R.id.btnDeleteSubTask);
            
            textSub.setText(subTitle);
            btnDelSub.setOnClickListener(v -> {
                task.getSubTasks().remove(subIndex);
                notifyItemChanged(position);
                actionListener.onDataChanged();
            });
            
            holder.subTaskContainer.addView(subView);
        }
    }

    private void showAddSubTaskDialog(android.content.Context context, Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Sub-note");

        final EditText input = new EditText(context);
        input.setHint("Enter sub-note...");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                task.addSubTask(text);
                notifyItemChanged(position);
                actionListener.onDataChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTask;
        ImageButton btnDelete, btnAddSubTask;
        ImageView imageTask;
        MaterialCardView cardView;
        LinearLayout subTaskContainer;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTask = itemView.findViewById(R.id.textTask);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddSubTask = itemView.findViewById(R.id.btnAddSubTask);
            imageTask = itemView.findViewById(R.id.imageTask);
            cardView = itemView.findViewById(R.id.cardView);
            subTaskContainer = itemView.findViewById(R.id.subTaskContainer);
        }
    }
}
