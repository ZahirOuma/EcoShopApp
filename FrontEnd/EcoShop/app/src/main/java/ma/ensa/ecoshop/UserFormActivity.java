package ma.ensa.ecoshop;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.ecoshop.R;
import ma.ensa.ecoshop.data.AppDatabase;
import ma.ensa.ecoshop.model.User;
import ma.ensa.ecoshop.utils.SharedPreferencesManager;

public class UserFormActivity extends AppCompatActivity {

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);


        sharedPreferencesManager = new SharedPreferencesManager(this);
        ImageView imageView = findViewById(R.id.imageView);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText firstNameInput = findViewById(R.id.firstNameInput);
        EditText lastNameInput = findViewById(R.id.lastNameInput);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer un utilisateur
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            // Utiliser la nouvelle méthode pour insérer et naviguer
            insertUserAndNavigate(user);
        });

        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;
            animatedVectorDrawable.start(); // Démarrer l'animation
        }
    }

    private void insertUserAndNavigate(User user) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        new Thread(() -> {
            // Insérer l'utilisateur
            db.userDao().insertUser(user);

            // Récupérer l'utilisateur nouvellement inséré par son email
            User insertedUser = db.userDao().getUserByEmail(user.getEmail());

            if (insertedUser != null) {
                // Sauvegarder l'ID dans SharedPreferences
                sharedPreferencesManager.saveUserId(insertedUser.getUserId());

                runOnUiThread(() -> {
                    Intent intent = new Intent(this, FirstActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        }).start();
    }
}