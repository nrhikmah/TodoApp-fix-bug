package hikmah.nur.todolist.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import hikmah.nur.todolist.R
import hikmah.nur.todolist.data.database.TodoRecord
import hikmah.nur.todolist.utilities.convertMillis
import hikmah.nur.todolist.utilities.convertNumberToMonthName
import kotlinx.android.synthetic.main.item_todo_list.view.*
import java.util.*

class TodoListAdapter(todoItemClickListener: TodoItemClickListener) :
    RecyclerView.Adapter<TodoListAdapter.ViewHolder>(), Filterable {

    private var todoRecordList: List<TodoRecord> = arrayListOf()
    private var filteredTodoRecordList: List<TodoRecord> = arrayListOf()
    private val listener: TodoItemClickListener = todoItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredTodoRecordList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(filteredTodoRecordList[position], listener)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()

                filteredTodoRecordList = if (charString.isEmpty()) {
                    todoRecordList
                } else {
                    val filteredList = arrayListOf<TodoRecord>()
                    for (item in todoRecordList) {
                        if (item.description?.toLowerCase(Locale.getDefault())!!.contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                            || item.title.toLowerCase(Locale.getDefault()).contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                            || item.tags?.toLowerCase(Locale.getDefault())!!.contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTodoRecordList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredTodoRecordList = results?.values as List<TodoRecord>
                notifyDataSetChanged()
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(todoRecord: TodoRecord, listener: TodoItemClickListener) {
            itemView.tv_item_title.text = todoRecord.title
            itemView.checkbox_item.isChecked = todoRecord.completed

            if (todoRecord.completed) {
                // Strike through the text to give an indicator that task is completed.
                itemView.tv_item_title.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                itemView.tv_item_due_date.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                itemView.tv_due_date.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            } else {
                itemView.tv_item_title.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                itemView.tv_item_due_date.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                itemView.tv_due_date.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            if (todoRecord.dueTime!!.toInt() != 0) {
                val dateValues =
                    convertMillis(todoRecord.dueTime)
                val displayFormat: String

                if (dateValues[4] < 10) {
                    displayFormat = String
                        .format(
                            itemView.context.getString(R.string.due_date_minute_less_than_ten),
                            convertNumberToMonthName(
                                dateValues[1]
                            ),
                            dateValues[0],
                            dateValues[2],
                            dateValues[3],
                            dateValues[4]
                        )
                } else {
                    displayFormat = String
                        .format(
                            itemView.context.getString(R.string.due_date_minute_greater_than_ten),
                            convertNumberToMonthName(
                                dateValues[1]
                            ),
                            dateValues[0],
                            dateValues[2],
                            dateValues[3],
                            dateValues[4]
                        )
                }

                itemView.tv_item_due_date.text = displayFormat
            } else {
                itemView.tv_item_due_date.text =
                    itemView.context.getString(R.string.no_due_is_set)
            }

            itemView.setOnClickListener {
                listener.onItemClicked(todoRecord)
            }

            itemView.checkbox_item.setOnClickListener {
                listener.onCheckClicked(todoRecord)
            }

            itemView.iv_delete_item.setOnClickListener {
                listener.onDeleteClicked(todoRecord)
            }
        }
    }

    fun setTodoItems(todoRecords: List<TodoRecord>) {
        this.todoRecordList = todoRecords
        this.filteredTodoRecordList = todoRecords
        notifyDataSetChanged()
    }

    interface TodoItemClickListener {
        fun onDeleteClicked(todoRecord: TodoRecord)
        fun onItemClicked(todoRecord: TodoRecord)
        fun onCheckClicked(todoRecord: TodoRecord)
    }
}