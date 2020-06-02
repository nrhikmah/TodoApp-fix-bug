package hikmah.nur.todolist.data.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TodoDao {

    @Insert
    suspend fun saveTodoItem(todoRecord: TodoRecord)

    @Insert
    suspend fun saveTodoItems(todoRecords: List<TodoRecord>)

    @Delete
    suspend fun deleteTodoItem(todoRecord: TodoRecord)

    @Update
    suspend fun updateTodoItem(todoRecord: TodoRecord)

    @Query("SELECT * FROM todo ORDER BY id DESC")
    fun getAllTodoList(): LiveData<MutableList<TodoRecord>>

}