package com.example.movie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getDatabase(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                )
                .addCallback(MovieDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MovieDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.movieDao()
                    
                    dao.insertMovie(Movie(
                        title = "The Dark Knight",
                        year = "2008",
                        rating = 5,
                        notes = "Siêu phẩm hành động về cuộc đối đầu kinh điển giữa Batman và Joker.",
                        imageUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg"
                    ))
                    
                    dao.insertMovie(Movie(
                        title = "Avatar",
                        year = "2009",
                        rating = 5,
                        notes = "Bộ phim mở ra cuộc cách mạng về kỹ xảo điện ảnh và thế giới Pandora kỳ vĩ.",
                        imageUrl = "https://m.media-amazon.com/images/M/MV5BZDA0OGQxNTItMDZkMC00N2UyLTg3MzMtYTJmNjg3Nzk5MzRiXkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_.jpg"
                    ))
                    
                    dao.insertMovie(Movie(
                        title = "Avengers: Endgame",
                        year = "2019",
                        rating = 5,
                        notes = "Trận chiến cuối cùng đầy cảm xúc để giải cứu vũ trụ của các siêu anh hùng Marvel.",
                        imageUrl = "https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_.jpg"
                    ))
                }
            }
        }
    }
}
