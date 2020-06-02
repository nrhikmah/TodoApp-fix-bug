package hikmah.nur.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hikmah.nur.todolist.data.TodoItemRepository
import hikmah.nur.todolist.data.database.TodoRecord

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoItemRepository =
        TodoItemRepository(application)
    private val todoItems: LiveData<MutableList<TodoRecord>> = repository.getAllTodoList()

    fun saveTodoItem(todoRecord: TodoRecord) {
        repository.saveTodoItem(todoRecord)
    }

    fun saveTodoItems(todoRecords: List<TodoRecord>) {
        repository.saveTodoItems(todoRecords)
    }

    fun updateTodoItem(todoRecord: TodoRecord) {
        repository.updateTodoItem(todoRecord)
    }

    fun deleteTodoItem(todoRecord: TodoRecord) {
        repository.deleteTodoItem(todoRecord)
    }

    fun toggleCompleteState(todoRecord: TodoRecord) {
        todoRecord.completed = !todoRecord.completed
        repository.updateTodoItem(todoRecord)
    }

    fun getAllTodoItemList(): LiveData<MutableList<TodoRecord>> {
        return todoItems
    }
}