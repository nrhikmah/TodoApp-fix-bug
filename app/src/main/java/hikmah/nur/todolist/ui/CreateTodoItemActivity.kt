package hikmah.nur.todolist.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hikmah.nur.todolist.R
import hikmah.nur.todolist.data.database.TodoRecord
import hikmah.nur.todolist.notification.NotificationUtils
import hikmah.nur.todolist.utilities.Constants
import hikmah.nur.todolist.utilities.convertMillis
import hikmah.nur.todolist.utilities.convertNumberToMonthName
import hikmah.nur.todolist.utilities.dateToMillis
import kotlinx.android.synthetic.main.activity_create_todo_item.*
import java.util.*

class CreateTodoItemActivity : AppCompatActivity() {

    private var mDueMonth: Int = 0
    private var mDueDay: Int = 0
    private var mDueYear: Int = 0
    private var mDueHour: Int = 0
    private var mDueMinute: Int = 0

    private var dueDate: Long = 0

    private var dateSelected = false
    private var timeSelected = false

    var todoRecord: TodoRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo_item)

        val intent = intent
        if (intent != null && intent.hasExtra(Constants.KEY_INTENT)) {
            val todoRecord: TodoRecord = intent.getParcelableExtra(
                Constants.KEY_INTENT)
            this.todoRecord = todoRecord

            if (todoRecord.dueTime!!.toInt() != 0) {
                dateSelected = true
                timeSelected = true
                val list =
                    convertMillis(todoRecord.dueTime)

                mDueDay = list[0]
                mDueMonth = list[1]
                mDueYear = list[2]
                mDueHour = list[3]
                mDueMinute = list[4]
            }

            fillUIWithItemData(todoRecord)
        }

        tv_todo_due_date.setOnClickListener {
            showDatePickerDialog()
        }

        tv_todo_due_time.setOnClickListener {
            showTimePickerDialog()
        }

        title =
            if (todoRecord != null) getString(R.string.edit_item) else getString(
                R.string.create_item
            )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.save_todo_item -> {
                setDueDateInMillis()

                saveTodoItem()
            }
        }
        return true
    }

    private fun saveTodoItem() {
        if (validateFields()) {
            val id = if (todoRecord != null) todoRecord?.id else null
            val todo = TodoRecord(
                id = id,
                title = et_todo_title.text.toString(),
                description = et_todo_description.text.toString(),
                tags = et_todo_tags.text.toString(),
                dueTime = dueDate,
                completed = todoRecord?.completed ?: false
            )

            val intent = Intent()
            intent.putExtra(Constants.KEY_INTENT, todo)
            setResult(RESULT_OK, intent)

            if (todo.dueTime!! > 0) {
                NotificationUtils()
                    .setNotification(todo, this)
            }

            finish()
        }
    }

    private fun validateFields(): Boolean {
        if (et_todo_title.text.isEmpty()) {
            til_todo_title.error = "Please enter title"
            et_todo_title.requestFocus()
            return false
        }
        if (et_todo_description.text.isEmpty()) {
            til_todo_description.error = "Please enter description"
            et_todo_description.requestFocus()
            return false
        }
        if (et_todo_tags.text.isEmpty()) {
            til_todo_tags.error = "Please provide at least one tag"
            et_todo_tags.requestFocus()
            return false
        }
        Toast.makeText(this, "Item is saved successfully.", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun setDueDateInMillis() {
        if (timeSelected && !dateSelected) {
            mDueYear = Calendar.getInstance().get(Calendar.YEAR)
            mDueMonth = Calendar.getInstance().get(Calendar.MONTH)
            mDueDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            dueDate = dateToMillis(
                mDueDay,
                mDueMonth,
                mDueYear,
                mDueMinute,
                mDueHour
            )

        } else if (!timeSelected && dateSelected) {
            mDueHour = 0
            mDueMinute = 0

            dueDate = dateToMillis(
                mDueDay,
                mDueMonth,
                mDueYear,
                mDueMinute,
                mDueHour
            )
        } else if (timeSelected && dateSelected) {
            dueDate = dateToMillis(
                mDueDay,
                mDueMonth,
                mDueYear,
                mDueMinute,
                mDueHour
            )
        }
    }

    private fun showDatePickerDialog() {
        mDueDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        mDueMonth = Calendar.getInstance().get(Calendar.MONTH)
        mDueYear = Calendar.getInstance().get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                tv_todo_due_date.text =
                    ("""Due Date: ${convertNumberToMonthName(
                        monthOfYear
                    )} $dayOfMonth $year""")

                mDueDay = dayOfMonth
                mDueMonth = monthOfYear
                mDueYear = year
                dateSelected = true
            },
            mDueYear,
            mDueMonth,
            mDueDay
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        mDueHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mDueMinute = Calendar.getInstance().get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mDueHour = hourOfDay
                mDueMinute = minute

                val displayFormat: String = if (mDueMinute < 10 && mDueHour < 10) {
                    """Due time: 0$hourOfDay : 0$mDueMinute"""
                } else {
                    if (mDueMinute > 10 ) {
                        """Due time: $hourOfDay : $mDueMinute"""
                    } else {
                        """Due time: $hourOfDay : 0$mDueMinute"""
                    }
                }

                tv_todo_due_time.text = displayFormat
                timeSelected = true

            }, mDueHour, mDueMinute, true)
        timePickerDialog.show()
    }

    private fun fillUIWithItemData(todoRecord: TodoRecord) {
        et_todo_title.setText(todoRecord.title, TextView.BufferType.EDITABLE)
        et_todo_description.setText(todoRecord.description, TextView.BufferType.EDITABLE)
        et_todo_tags.setText(todoRecord.tags, TextView.BufferType.EDITABLE)

        if (todoRecord.dueTime!!.toInt() != 0) {
            val dateValues =
                convertMillis(todoRecord.dueTime)

            val dueMonth =
                convertNumberToMonthName(
                    dateValues[1]
                )

            val dueYear = dateValues[2].toString()

            val dueHour = if (dateValues[3] < 10) {
                "0${dateValues[3]}"
            } else {
                "${dateValues[3]}"
            }

            val dueMinute = if (dateValues[4] < 10) {
                "0${dateValues[4]}"
            } else {
                "${dateValues[4]}"
            }

            tv_todo_due_date.text = """${dueMonth} ${dateValues[0]} ${dueYear}"""
            tv_todo_due_time.text = """${dueHour} : ${dueMinute}"""
        }
    }
}
