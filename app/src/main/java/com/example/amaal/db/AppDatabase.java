package com.example.amaal.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import com.example.amaal.model.Deed;
import com.example.amaal.model.FiqhCategory;

@Database(entities = {Deed.class}, version = 1, exportSchema = false)
@TypeConverters({AppDatabase.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DeedDao deedDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "amaal_database")
                            .allowMainThreadQueries() 
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static class Converters {
        @TypeConverter
        public static FiqhCategory fromString(String value) {
            return value == null ? null : FiqhCategory.valueOf(value);
        }

        @TypeConverter
        public static String categoryToString(FiqhCategory category) {
            return category == null ? null : category.name();
        }
    }
}