package eu.morningbird.storytellingengine.model.converter

import androidx.room.TypeConverter
import eu.morningbird.storytellingengine.model.Position

class PositionConverter {
    @TypeConverter
    fun getStatus(numeral: Int): Position {
        for (ds in Position.values()) {
            if (ds.code == numeral) {
                return ds
            }
        }
        return Position.LEFT
    }

    @TypeConverter
    fun getStatusInt(status: Position): Int {
        return status.code
    }
}