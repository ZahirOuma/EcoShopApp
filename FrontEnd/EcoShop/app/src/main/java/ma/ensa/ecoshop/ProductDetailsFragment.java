package ma.ensa.ecoshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ma.ensa.ecoshop.databinding.FragmentProductDetailsBinding;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.service.ProductViewModel;
import ma.ensa.ecoshop.service.ProductViewModelFactory;

public class ProductDetailsFragment extends Fragment {
    private Product product; // Gardez la référence
    private ProductViewModel productViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        ProductViewModelFactory factory = new ProductViewModelFactory(requireContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        String barcode = getArguments().getString("barcode");

        productViewModel.getProductByBarcode(barcode).observe(getViewLifecycleOwner(), newProduct -> {
            if (newProduct != null) {
                this.product = newProduct; // Sauvegardez la référence
                setupViews(view, newProduct);
            }
        });

        return view;
    }

    private void setupViews(View view, Product product) {
        if (product == null) return;

        // Configuration de la première card
        CardView mainCard = view.findViewById(R.id.mainCard);
        ImageView productImage = view.findViewById(R.id.productImage);
        TextView productName = view.findViewById(R.id.productName);
        TextView brandName = view.findViewById(R.id.brandName);
        TextView quantity = view.findViewById(R.id.quantity);
        ImageView nutriScoreImage = view.findViewById(R.id.nutriScoreImage);
        TextView nutriScoreQuality = view.findViewById(R.id.nutriScoreQuality);
        ImageView ecoScoreImage = view.findViewById(R.id.ecoScoreImage);
        TextView ecoScoreQuality = view.findViewById(R.id.ecoScoreQuality);

        // Setup image
        if (product.getProductImage() != null && product.getProductImage().getImageUrl() != null) {
            Glide.with(requireContext())
                    .load(product.getProductImage().getImageUrl())
                    .into(productImage);
        }

        // Setup des informations basiques
        Product.ProductDetails details = product.getProductDetails();
        if (details != null) {
            productName.setText(details.getProductName() != null ? details.getProductName() : "");
            brandName.setText(details.getBrands() != null ? details.getBrands() : "");
            quantity.setText(details.getQuantity() != null ? details.getQuantity() : "");
        }

        // Gestion de l'Eco-Score
        if (product.getEnvironmentalImpact() != null) {
            String greenScore = product.getEnvironmentalImpact().getGreenScore();
            if (greenScore != null) {
                Pattern pattern = Pattern.compile("([A-E])");
                Matcher matcher = pattern.matcher(greenScore);
                if (matcher.find()) {
                    String scoreLetter = matcher.group(1);
                    String environmentalDescription = "";

                    switch (scoreLetter) {
                        case "A":
                            environmentalDescription = "Très faible impact environnemental A";
                            break;
                        case "B":
                            environmentalDescription = "Faible impact environnemental B";
                            break;
                        case "C":
                            environmentalDescription = "Impact modéré sur l'environnement C";
                            break;
                        case "D":
                            environmentalDescription = "Impact environnemental élevé D";
                            break;
                        case "E":
                            environmentalDescription = "Impact environnemental très élevé E";
                            break;
                        default:
                            environmentalDescription = "Impact environnemental inconnu";
                    }

                    int ecoScoreDrawableId = getResources().getIdentifier(
                            scoreLetter.toLowerCase() + "_eco",
                            "drawable",
                            requireContext().getPackageName()
                    );

                    if (ecoScoreDrawableId != 0) {
                        ecoScoreImage.setImageResource(ecoScoreDrawableId);
                    }
                    ecoScoreQuality.setText(environmentalDescription);
                } else {
                    ecoScoreQuality.setText("Impact environnemental non disponible");
                }
            }
        }




        // Gestion du Nutri-Score
        if (product.getHealthInfo() != null && product.getHealthInfo().getNutriscore() != null) {
            String nutriscoreGrade = product.getHealthInfo().getNutriscore().getGrade();
            String nutriscoreQualityText = product.getHealthInfo().getNutriscore().getQuality();

            if (nutriscoreGrade != null && nutriscoreGrade.startsWith("Nutri-Score ")) {
                char scoreChar = nutriscoreGrade.charAt(12);
                int nutriScoreDrawableId = getResources().getIdentifier(
                        String.valueOf(scoreChar).toLowerCase() + "_nutri",
                        "drawable",
                        requireContext().getPackageName()
                );

                if (nutriScoreDrawableId != 0) {
                    nutriScoreImage.setImageResource(nutriScoreDrawableId);
                }
            }
            nutriScoreQuality.setText(nutriscoreQualityText != null ? nutriscoreQualityText : "Qualité inconnue");
        }

        // Setup des click listeners pour les cards
        CardView environmentalCard = view.findViewById(R.id.environmentalCard);
        CardView healthCard = view.findViewById(R.id.healthCard);

        if (environmentalCard != null && healthCard != null) {
            environmentalCard.setOnClickListener(v -> scrollToCard(view, R.id.environmentalInfoCard));
            healthCard.setOnClickListener(v -> scrollToCard(view, R.id.healthInfoCard));
        }
        setupEnvironmentalCard(view,product);
        setupIngredientsCard(view, product);

        setupHealthCard(view,product);
    }

    private void setupIngredientsCard(View view, Product product) {
        if (product == null || product.getProductDetails() == null) return;

        TextView ingredientsText = view.findViewById(R.id.ingredientsText);
        String ingredients = product.getProductDetails().getIngredients();

        if (ingredients != null && !ingredients.isEmpty()) {
            ingredientsText.setText(ingredients);
        } else {
            ingredientsText.setText("Aucun ingrédient listé");
        }
    }

    private void setupEnvironmentalCard(View view, Product product) {
        if (product == null) return;

        ImageView ecoScoreImage = view.findViewById(R.id.ecoScoreImageDetail);
        TextView ecoScoreGrade = view.findViewById(R.id.ecoScoreGrade);
        TextView co2Text = view.findViewById(R.id.co2Text);
        TextView descriptionText = view.findViewById(R.id.descriptionText);
        CardView ecoScoreDetailCard = view.findViewById(R.id.ecoScoreDetailCard); // Ajoutez la carte ici

        if (product.getEnvironmentalImpact() != null) {
            String greenScore = product.getEnvironmentalImpact().getGreenScore();
            if (greenScore != null) {
                Pattern pattern = Pattern.compile("([A-E])");
                Matcher matcher = pattern.matcher(greenScore);
                if (matcher.find()) {
                    String scoreLetter = matcher.group(1);
                    int ecoScoreDrawableId = getResources().getIdentifier(
                            scoreLetter.toLowerCase() + "_eco",
                            "drawable",
                            requireContext().getPackageName()
                    );
                    if (ecoScoreDrawableId != 0) {
                        ecoScoreImage.setImageResource(ecoScoreDrawableId);
                    }
                    ecoScoreGrade.setText("Green-Score " + scoreLetter + " - " +
                            getEnvironmentalImpactDescription(scoreLetter));
                }
            }
        }

        if (product.getCarbonFootprint() != null) {
            co2Text.setText(product.getCarbonFootprint().getCo2());
            descriptionText.setText(product.getCarbonFootprint().getDescription());
        }

        // Gestion du clic pour rediriger vers EcoScoreDetailFragment
        if (ecoScoreDetailCard != null) {
            ecoScoreDetailCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String barcode = product.getBarcode(); // Récupérez le code-barres du produit

                    // Créez un Bundle pour transmettre les données
                    Bundle bundle = new Bundle();
                    bundle.putString("barcode", barcode);

                    // Instanciez le fragment cible et définissez les arguments
                    EcoScoreDetailFragment ecoScoreFragment = new EcoScoreDetailFragment();
                    ecoScoreFragment.setArguments(bundle);

                    // Effectuez la transaction pour remplacer le fragment actuel
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, ecoScoreFragment)
                                .addToBackStack(null) // Permet de revenir au fragment précédent
                                .commit();
                    }
                }
            });
        }
    }

    private String getEnvironmentalImpactDescription(String score) {
        switch (score) {
            case "A": return "Très faible impact environnemental";
            case "B": return "Faible impact environnemental";
            case "C": return "Impact modéré sur l'environnement";
            case "D": return "Impact environnemental élevé";
            case "E": return "Impact environnemental très élevé";
            default: return "Impact environnemental inconnu";
        }
    }    private void setupHealthCard(View view, Product product) {
        if (product == null) return;

        TextView nutriscoreGrade = view.findViewById(R.id.nutriscoreGrade);
        TextView nutriscoreQuality = view.findViewById(R.id.nutriscoreQuality);
        ImageView nutriScoreImageDetail = view.findViewById(R.id.nutriScoreImageDetail);
        View nutriscoreDetailCard = view.findViewById(R.id.nutriscoreDetailCard);


        if (product.getHealthInfo() != null && product.getHealthInfo().getNutriscore() != null) {
            String nutriscoreGradeText = product.getHealthInfo().getNutriscore().getGrade();
            String nutriscoreQualityText = product.getHealthInfo().getNutriscore().getQuality();

            if (nutriscoreGradeText != null && nutriscoreGradeText.startsWith("Nutri-Score ")) {
                char scoreChar = nutriscoreGradeText.charAt(12);
                int nutriScoreDrawableId = getResources().getIdentifier(
                        String.valueOf(scoreChar).toLowerCase() + "_nutri",
                        "drawable",
                        requireContext().getPackageName()
                );

                if (nutriScoreDrawableId != 0) {
                    nutriScoreImageDetail.setImageResource(nutriScoreDrawableId);
                }
            }

            nutriscoreGrade.setText(nutriscoreGradeText != null ? nutriscoreGradeText : "Nutriscore: Non disponible");
            nutriscoreQuality.setText(nutriscoreQualityText != null ? nutriscoreQualityText : "Qualité: Non disponible");

            nutriscoreDetailCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String barcode = product.getBarcode();

                    Bundle bundle = new Bundle();
                    bundle.putString("barcode", barcode);

                    NutriScoreDetailFragment nutriScoreFragment = new NutriScoreDetailFragment();
                    nutriScoreFragment.setArguments(bundle);

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, nutriScoreFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        } else {
            nutriscoreGrade.setText("Nutriscore: Non disponible");
            nutriscoreQuality.setText("Qualité: Non disponible");
        }
    }
    private void scrollToCard(View view, int cardId) {
        NestedScrollView scrollView = view.findViewById(R.id.nestedScrollView);
        View targetCard = view.findViewById(cardId);

        if (scrollView != null && targetCard != null) {
            int[] location = new int[2];
            targetCard.getLocationInWindow(location);
            int targetY = location[1];
            scrollView.smoothScrollTo(0, targetY);
        }
    }
}