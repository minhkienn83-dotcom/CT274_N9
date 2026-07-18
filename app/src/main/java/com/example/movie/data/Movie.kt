package com.example.movie.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val year: String,
    val rating: Int,
    val notes: String = "",
    val imageUrl: String = "",
    val lastWatchedTime: String = "" // Thêm mốc thời gian đã xem (ví dụ: 01:20:00)
)
