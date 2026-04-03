package com.application.todolistapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Task> taskList;
    private TaskAdapter adapter;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "TodoListPrefs";
    private static final String KEY_TASKS_JSON = "tasks_json";

    private EditText editTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Preferences and Data
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadTasks();

        // Setup UI
        editTask = findViewById(R.id.editTask);
        Button btnAdd = findViewById(R.id.btnAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);

        // Add Main Heading Click Listener
        btnAdd.setOnClickListener(v -> {
            String title = editTask.getText().toString().trim();
            if (!title.isEmpty()) {
                Task newTask = new Task(title, getRandomColor());
                taskList.add(newTask);
                adapter.notifyItemInserted(taskList.size() - 1);
                editTask.setText("");
                saveTasks();
            } else {
                Toast.makeText(this, "Please enter a heading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getRandomColor() {
        int[] colors = {
                Color.parseColor("#E3F2FD"), // Blue
                Color.parseColor("#F1F8E9"), // Green
                Color.parseColor("#FFF3E0"), // Orange
                Color.parseColor("#FCE4EC"), // Pink
                Color.parseColor("#F3E5F5"), // Purple
                Color.parseColor("#E0F2F1")  // Teal
        };
        return colors[new Random().nextInt(colors.length)];
    }

    @Override
    public void onDeleteTask(int position) {
        taskList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, taskList.size());
        saveTasks();
    }

    @Override
    public void onDataChanged() {
        saveTasks();
    }

    private void saveTasks() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Task task : taskList) {
                JSONObject taskJson = new JSONObject();
                taskJson.put("title", task.getTitle());
                taskJson.put("color", task.getColor());
                
                JSONArray subTasksArray = new JSONArray();
                for (String sub : task.getSubTasks()) {
                    subTasksArray.put(sub);
                }
                taskJson.put("subTasks", subTasksArray);
                jsonArray.put(taskJson);
            }
            
            sharedPreferences.edit().putString(KEY_TASKS_JSON, jsonArray.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving tasks", e);
        }
    }

    private void loadTasks() {
        taskList = new ArrayList<>();
        String json = sharedPreferences.getString(KEY_TASKS_JSON, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject taskJson = jsonArray.getJSONObject(i);
                    int color = taskJson.optInt("color", Color.WHITE);
                    Task task = new Task(taskJson.getString("title"), color);
                    
                    JSONArray subTasksArray = taskJson.getJSONArray("subTasks");
                    for (int j = 0; j < subTasksArray.length(); j++) {
                        task.addSubTask(subTasksArray.getString(j));
                    }
                    taskList.add(task);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error loading tasks", e);
            }
        }
    }
}
