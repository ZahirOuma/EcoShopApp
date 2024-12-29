package ma.ensa.ecoshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.ecoshop.utils.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        // Trouver le bouton Get Started par son ID
        Button getStartedButton = findViewById(R.id.button2);

        // Ajouter un écouteur de clic sur le bouton
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérifier si l'utilisateur est enregistré
                if (sharedPreferencesManager.isUserRegistered()) {
                    // Si l'utilisateur est enregistré, rediriger vers ScanActivity
                    Intent intent = new Intent(SplashActivity.this, FirstActivity.class);
                    startActivity(intent);
                    finish(); // Ferme SplashActivity
                } else {
                    // Si l'utilisateur n'est pas enregistré, rediriger vers UserFormActivity
                    Intent intent = new Intent(SplashActivity.this, UserFormActivity.class);
                    startActivity(intent);
                    finish(); // Ferme SplashActivity
                }
            }
        });
    }
}