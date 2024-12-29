package ma.ensa.ecoshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ma.ensa.ecoshop.adapter.PointsAdapter;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.service.ProductViewModel;
import ma.ensa.ecoshop.service.ProductViewModelFactory;

public class NutriScoreDetailFragment extends Fragment {
    private RecyclerView negativePointsList;
    private RecyclerView positivePointsList;
    private PointsAdapter negativeAdapter;
    private PointsAdapter positiveAdapter;
    private ProductViewModel productViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Charger la vue
        View view = inflater.inflate(R.layout.fragment_nutriscore_detail, container, false);
        ImageView backIcon = view.findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(); // Retour au fragment précédent
        });

        // Initialiser le ViewModel
        ProductViewModelFactory factory = new ProductViewModelFactory(requireContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        // Initialiser les RecyclerViews
        negativePointsList = view.findViewById(R.id.negativePointsList);
        positivePointsList = view.findViewById(R.id.positivePointsList);
        negativePointsList.setLayoutManager(new LinearLayoutManager(getContext()));
        positivePointsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtenir le code-barres depuis les arguments
        String barcode = getArguments() != null ? getArguments().getString("barcode") : null;

        // Charger les détails du produit
        if (barcode != null) {
            loadProductDetails(view, barcode);
        } else {
            Log.e("NutriScoreDetailFragment", "Barcode is null!");
        }

        return view;
    }

    private void loadProductDetails(View view, String barcode) {
        // Observez les données depuis le ViewModel
        productViewModel.getProductByBarcode(barcode).observe(getViewLifecycleOwner(), product -> {
            // Vérifiez si le produit et ses informations de santé ne sont pas null
            if (product != null && product.getHealthInfo() != null) {
                Product.HealthInfo healthInfo = product.getHealthInfo();

                // Points négatifs
                if (healthInfo.getNegativePoints() != null) {
                    negativeAdapter = new PointsAdapter(healthInfo.getNegativePoints().getComponents());
                    negativePointsList.setAdapter(negativeAdapter);
                }

                // Points positifs
                if (healthInfo.getPositivePoints() != null) {
                    positiveAdapter = new PointsAdapter(healthInfo.getPositivePoints().getComponents());
                    positivePointsList.setAdapter(positiveAdapter);
                }

                // En-têtes pour les sections des points positifs et négatifs
                TextView negativeHeader = view.findViewById(R.id.negativePointsHeader);
                TextView positiveHeader = view.findViewById(R.id.positivePointsHeader);
                if (negativeHeader != null && positiveHeader != null) {
                    negativeHeader.setText(
                            healthInfo.getNegativePoints() != null ? healthInfo.getNegativePoints().getTitle() : "Points négatifs"
                    );
                    positiveHeader.setText(
                            healthInfo.getPositivePoints() != null ? healthInfo.getPositivePoints().getTitle() : "Points positifs"
                    );
                }

                // Affichage des détails de la carte Nutri-Score
                TextView nutriscoreGrade = view.findViewById(R.id.nutriscoreGrade);
                TextView nutriscoreQuality = view.findViewById(R.id.nutriscoreQuality);
                ImageView nutriScoreImageDetail = view.findViewById(R.id.nutriScoreImageDetail);

                // Vérifiez si les vues Nutri-Score sont valides
                if (nutriscoreGrade != null && nutriscoreQuality != null && nutriScoreImageDetail != null) {
                    // Vérifiez si les données Nutri-Score sont disponibles
                    if (healthInfo.getNutriscore() != null) {
                        String nutriscoreGradeText = healthInfo.getNutriscore().getGrade();
                        String nutriscoreQualityText = healthInfo.getNutriscore().getQuality();

                        // Gestion du caractère de grade pour l'image
                        if (nutriscoreGradeText != null && nutriscoreGradeText.startsWith("Nutri-Score ") && nutriscoreGradeText.length() > 12) {
                            char scoreChar = nutriscoreGradeText.charAt(12);
                            int nutriScoreDrawableId = getResources().getIdentifier(
                                    String.valueOf(scoreChar).toLowerCase() + "_nutri",
                                    "drawable",
                                    requireContext().getPackageName()
                            );

                            // Charge l'image si le drawable est trouvé
                            if (nutriScoreDrawableId != 0) {
                                nutriScoreImageDetail.setImageResource(nutriScoreDrawableId);
                            } else {
                                Log.e("NutriScore", "Drawable introuvable pour le grade: " + scoreChar);
                            }
                        } else {
                            Log.e("NutriScore", "Format inattendu pour le grade Nutri-Score: " + nutriscoreGradeText);
                        }

                        // Mettre à jour les textes du Nutri-Score
                        nutriscoreGrade.setText(
                                nutriscoreGradeText != null ? nutriscoreGradeText : "Nutriscore: Non disponible"
                        );
                        nutriscoreQuality.setText(
                                nutriscoreQualityText != null ? nutriscoreQualityText : "Qualité: Non disponible"
                        );
                    }
                } else {
                    Log.e("NutriScoreDetailFragment", "Une ou plusieurs vues Nutri-Score sont null !");
                }
            } else {
                Log.e("NutriScoreDetailFragment", "Produit ou HealthInfo est null !");
            }
        });

    }
}
