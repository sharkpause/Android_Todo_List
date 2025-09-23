package com.example.to_do_list_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = ""
)

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
    val taskList = remember { mutableStateListOf<Task>() }

    Column(modifier = Modifier.padding(padding)) {
        Row(modifier = Modifier
            .padding(bottom = 30.dp)
            .fillMaxWidth()) {
            TaskInput(
                task = task,
                onTaskInputChange = { task = it },
                modifier = Modifier.widthIn(max = 200.dp)
            )
            AddTaskButton(
                onClick = {
                    taskList.add(Task(title = task))
                }
            )
        }

        Column {
            TaskList(taskList = taskList)
        }
    }
}

@Composable
fun TaskInput(task: String, onTaskInputChange: (String) -> Unit, modifier: Modifier) {
    TextField(
        value = task,
        onValueChange = { onTaskInputChange(it) },
        label = { Text("Enter the title of your task") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun AddTaskButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Add task")
    }
}

@Composable
fun TaskList(taskList: SnapshotStateList<Task>) {
    LazyColumn {
        items(taskList.size) { index ->
            var buttonsWidth by remember { mutableFloatStateOf(0f) }
            var deleteTargetID = remember { "" }

            SwipeableItemWithActions(
                isRevealed = false,
                actions = {
                    Box {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            ActionButton(
                                contentDescription = "Delete",
                                icon = R.drawable.ic_delete_outline,
                                onClick = {
                                    taskList.removeIf { it.id == deleteTargetID }
                                },
                                set = { buttonsWidth += it },
                                modifier = Modifier.background(Color(0xFFe74c3c))
                            )
                            ActionButton(
                                contentDescription = "Edit",
                                icon = R.drawable.ic_edit_outline,
                                onClick = {},
                                set = { buttonsWidth += it },
                                modifier = Modifier.background(Color(0xFF3498db))
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                onCollapsed = {},
                onExpanded = {},
                content = { TaskItem(task = taskList[index], set = { deleteTargetID = it }) },
                buttonsWidth = buttonsWidth
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, set: (String) -> Unit) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp))
    ) {
        set(task.id)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = task.title,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: Int,
    onClick: () -> Unit,
    contentDescription: String,
    set: (Float) -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.onSizeChanged { size ->
            set(size.width.toFloat())
        }
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableItemWithActions(
    isRevealed: Boolean,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    content: @Composable () -> Unit,
    buttonsWidth: Float
) {
    var contextMenuWidth by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(contextMenuWidth) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = isRevealed, contextMenuWidth) {
        if(isRevealed) {
            offset.animateTo(contextMenuWidth)
        } else {
            offset.animateTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .onSizeChanged {
                    contextMenuWidth = it.width.toFloat()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = -offset.value.roundToInt(), y = 0) }
                .pointerInput(true) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset =
                                    (offset.value - dragAmount).coerceIn(0f, buttonsWidth)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            Log.d("Offset value", "${offset.value}")
                            Log.d("Buttons width", "$buttonsWidth")
                            when {
                                offset.value > buttonsWidth / 2f -> {
                                    scope.launch {
                                        offset.animateTo(buttonsWidth)
                                        onExpanded()
                                    }
                                }

                                else -> {
                                    scope.launch {
                                        offset.animateTo(0f)
                                        onCollapsed()
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}