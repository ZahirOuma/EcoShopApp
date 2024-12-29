package ma.ensa.ecoshop;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import ma.ensa.ecoshop.ListFragment;
import ma.ensa.ecoshop.ProfileFragment;
import ma.ensa.ecoshop.R;
import ma.ensa.ecoshop.ScannerFragment;

public class FirstActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Configuration du BottomNavigationView
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_profile) {
                    loadFragment(new ProfileFragment());
                    return true;
                } else if (itemId == R.id.navigation_list) {
                    loadFragment(new ListFragment());
                    return true;
                } else if (itemId == R.id.navigation_scanner) {
                    loadFragment(new ScannerFragment());
                    return true;
                }
                return false;
            }
        });

        // Charger ScannerFragment par défaut au démarrage si c'est la première création de l'activité
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.navigation_scanner);
        }
    }

    // Méthode pour charger un fragment dans le conteneur
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}