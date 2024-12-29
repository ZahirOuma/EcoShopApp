package ma.ensa.ecoshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class AppInfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);

        // Animation de la bulle de cœur
        ImageView heartBubble = view.findViewById(R.id.heartBubble);
        Animation heartAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.heart_animation);
        heartBubble.startAnimation(heartAnimation);

        // Bouton retour
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}