package ma.ensa.ecoshop.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ma.ensa.ecoshop.dao.ProductDao;
import ma.ensa.ecoshop.dao.UserDao;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.model.User;

@Database(entities = {Product.class, User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() // Option pour ignorer les données
                            .build();
                }
            }
        }
        return instance;
    }
}
