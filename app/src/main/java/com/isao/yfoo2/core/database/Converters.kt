package com.isao.yfoo2.core.database

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun instantToTimestamp(instant: Instant): Long {
        return instant.toEpochMilli()
    }

    @TypeConverter
    fun fromTimestamp(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }
}