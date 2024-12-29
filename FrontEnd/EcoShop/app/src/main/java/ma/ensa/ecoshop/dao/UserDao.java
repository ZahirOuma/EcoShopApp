package ma.ensa.ecoshop.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import ma.ensa.ecoshop.model.User;
@Dao
public interface UserDao {
    @Insert
    void insertUser(User user); // Insérer un utilisateur

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    User getUserById(int userId);
    // Récupérer un utilisateur par son ID
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
    @Update
    void update(User user);
}