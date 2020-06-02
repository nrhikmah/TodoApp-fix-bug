package hikmah.nur.todolist.data

import android.app.Application
import androidx.lifecycle.LiveData
import hikmah.nur.todolist.data.database.TodoDatabase
import hikmah.nur.todolist.data.database.TodoRecord
import hikmah.nur.todolist.data.database.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoItemRepository(application: Application) {

    private val todoDao: TodoDao
    private val allTodoItems: LiveData<MutableList<TodoRecord>>

    init {
        val database = TodoDatabase.getInstance(application.applicationContext)
        todoDao = database!!.todoDao()
        allTodoItems = todoDao.getAllTodoList()
    }

    fun saveTodoItems(todoRecords: List<TodoRecord>) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.saveTodoItems(todoRecords)
        }
    }

    fun saveTodoItem(todoRecord: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.saveTodoItem(todoRecord)
        }
    }

    fun updateTodoItem(todoRecord: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.updateTodoItem(todoRecord)
        }
    }

    fun deleteTodoItem(todoRecord: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.deleteTodoItem(todoRecord)
        }
    }

    fun getAllTodoList(): LiveData<MutableList<TodoRecord>> {
        return allTodoItems
    }

}