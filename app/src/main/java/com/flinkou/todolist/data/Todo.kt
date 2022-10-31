package com.flinkou.todolist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Todo(
    @PrimaryKey val id: String,
    val text: String,
    val importance: String,
    val done: Boolean,
    val deadline: Long?,
    val creationTime: Long,
    val updateTime: Long,
) : Parcelable {
    companion object {
        var currentIndex = 0

        fun getNextID(): String {
            currentIndex++;
            return currentIndex.toString()
        }
    }
}