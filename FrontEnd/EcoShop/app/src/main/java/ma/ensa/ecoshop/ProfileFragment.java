package ma.ensa.ecoshop;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import ma.ensa.ecoshop.data.AppDatabase;
import ma.ensa.ecoshop.model.User;
import ma.ensa.ecoshop.utils.SharedPreferencesManager;

public class ProfileFragment extends Fragment {
    private SharedPreferencesManager sharedPreferencesManager;
    private TextView userName, userEmail;
    private ImageView profileImage;
    private CardView editProfileCard, shareUpCard, rateUpCard, appInfoCard;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadUserProfile();
        setupClickListeners();
    }

    private void initViews(View view) {
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        db = AppDatabase.getInstance(requireContext());

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);
        editProfileCard = view.findViewById(R.id.editProfileCard);
        shareUpCard = view.findViewById(R.id.shareUpCard);
        rateUpCard = view.findViewById(R.id.rateUpCard);
        appInfoCard = view.findViewById(R.id.appInfoCard);
    }

    private void loadUserProfile() {
        int userId = sharedPreferencesManager.getUserId();
        if (userId != -1) {
            new Thread(() -> {
                User user = db.userDao().getUserById(userId);
                if (user != null) {
                    requireActivity().runOnUiThread(() -> {
                        userName.setText(user.getFirstName() + " " + user.getLastName());
                        userEmail.setText(user.getEmail());
                    });
                }
            }).start();
        }
    }

    private void setupClickListeners() {
        editProfileCard.setOnClickListener(v -> openEditProfile());
        shareUpCard.setOnClickListener(v -> shareApp());
        rateUpCard.setOnClickListener(v -> showRatingDialog());
        appInfoCard.setOnClickListener(v -> openAppInfo());
    }

    private void openEditProfile() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_edit_profile);

        EditText firstNameEdit = dialog.findViewById(R.id.firstNameEdit);
        EditText lastNameEdit = dialog.findViewById(R.id.lastNameEdit);
        EditText emailEdit = dialog.findViewById(R.id.emailEdit);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        // Load current user data
        int userId = sharedPreferencesManager.getUserId();
        new Thread(() -> {
            User user = db.userDao().getUserById(userId);
            requireActivity().runOnUiThread(() -> {
                firstNameEdit.setText(user.getFirstName());
                lastNameEdit.setText(user.getLastName());
                emailEdit.setText(user.getEmail());
            });
        }).start();

        saveButton.setOnClickListener(v -> {
            String firstName = firstNameEdit.getText().toString().trim();
            String lastName = lastNameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                User user = db.userDao().getUserById(userId);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                db.userDao().update(user);

                requireActivity().runOnUiThread(() -> {
                    loadUserProfile();
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EcoShop App");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out EcoShop app - Your eco-friendly shopping companion! " +
                        "Download it from: [Your App Store Link]");
        startActivity(Intent.createChooser(shareIntent, "Share EcoShop via"));
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_rating);

        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();

            // Save rating logic here if needed

            Toast.makeText(requireContext(),
                    "Thank you for rating EcoShop!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void openAppInfo() {
        AppInfoFragment appInfoFragment = new AppInfoFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, appInfoFragment)
                .addToBackStack(null)
                .commit();
    }
}