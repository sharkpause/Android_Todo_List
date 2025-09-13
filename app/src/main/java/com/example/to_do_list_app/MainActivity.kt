package com.example.to_do_list_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import com.example.to_do_list_app.ui.theme.Todo_List_AppTheme

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.material3.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todo_List_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color(0xFFFEFFE5)) { innerPadding ->
                    MainScreen(padding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainScreen(padding: PaddingValues) {
    var task by remember { mutableStateOf("") }
    var taskList = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(padding)) {
        Row(modifier = Modifier.padding(bottom = 30.dp).fillMaxWidth()) {
            TaskInput(
                task = task,
                onTaskInputChange = { task = it }
            )
            AddTaskButton(
                onClick = {
                    taskList.add(task)
                }
            )
        }

        Column {
            TaskList(taskList = taskList)
        }
    }
}

@Composable
fun TaskInput(task: String, onTaskInputChange: (String) -> Unit) {
    TextField(
        value = task,
        onValueChange = { onTaskInputChange(it) },
        label = { Text("Enter your task:") },
    )
}

@Composable
fun AddTaskButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Add task")
    }
}

@Composable
fun TaskList(taskList: SnapshotStateList<String>) {
    LazyColumn {
        items(taskList.size) { index ->
            TaskItem(taskText = taskList[index])
        }
    }
}

@Composable
fun TaskItem(taskText: String) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = taskText,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}