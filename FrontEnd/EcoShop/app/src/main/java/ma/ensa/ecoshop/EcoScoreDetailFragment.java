package ma.ensa.ecoshop;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.service.ProductViewModel;
import ma.ensa.ecoshop.service.ProductViewModelFactory;

public class EcoScoreDetailFragment extends Fragment {

    private ProductViewModel productViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eco_score_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurer l'icône de retour
        ImageView backIcon = view.findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(); // Retour au fragment précédent
        });

        // Initialiser le ViewModel
        ProductViewModelFactory factory = new ProductViewModelFactory(requireContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        // Obtenir le code-barres depuis les arguments
        String barcode = getArguments() != null ? getArguments().getString("barcode") : null;

        // Charger les détails du produit
        if (barcode != null) {
            loadProductDetails(view, barcode);
        } else {
            Log.e("EcoScoreDetailFragment", "Barcode is null!");
        }
    }

    private void loadProductDetails(View view, String barcode) {
        productViewModel.getProductByBarcode(barcode).observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                setupViews(view, product);
            } else {
                Log.e("EcoScoreDetailFragment", "Produit introuvable pour le code-barres: " + barcode);
            }
        });
    }

    private void setupViews(View view, Product product) {
        TextView greenScoreText = view.findViewById(R.id.greenScoreText);
        TextView categoryText = view.findViewById(R.id.categoryText);
        TextView liTexts = view.findViewById(R.id.liTexts);
        TextView descriptionText = view.findViewById(R.id.descriptionText);
        TextView co2Text = view.findViewById(R.id.co2Text);
        TableLayout impactsTableEnvironmental = view.findViewById(R.id.impactsTableEnvironmental);
        TableLayout impactsTableCarbon = view.findViewById(R.id.impactsTableCarbon);

        setupEnvironmentalImpact(product, greenScoreText, categoryText, liTexts, impactsTableEnvironmental);
        setupCarbonFootprint(product, descriptionText, co2Text, impactsTableCarbon);
    }

    private void setupEnvironmentalImpact(Product product, TextView greenScoreText,
                                          TextView categoryText, TextView liTexts,
                                          TableLayout impactsTableEnvironmental) {
        if (product.getEnvironmentalImpact() == null) return;

        greenScoreText.setText(product.getEnvironmentalImpact().getGreenScore());
        categoryText.setText(product.getProductDetails().getCategories());

        if (product.getEnvironmentalImpact().getLiTexts() != null) {
            StringBuilder liTextsBuilder = new StringBuilder();
            for (String text : product.getEnvironmentalImpact().getLiTexts()) {
                liTextsBuilder.append("• ").append(text).append("\n");
            }
            liTexts.setText(liTextsBuilder.toString().trim());
        }

        setupImpactsTable(impactsTableEnvironmental, product.getEnvironmentalImpact().getImpactsByStage());
    }

    private void setupCarbonFootprint(Product product, TextView descriptionText,
                                      TextView co2Text, TableLayout impactsTableCarbon) {
        if (product.getCarbonFootprint() == null) return;

        descriptionText.setText(product.getCarbonFootprint().getDescription());
        co2Text.setText(product.getCarbonFootprint().getCo2());
        setupImpactsTable(impactsTableCarbon, product.getCarbonFootprint().getImpactsByStage());
    }

    private void setupImpactsTable(TableLayout table, Map<String, String> impacts) {
        if (impacts == null) return;

        for (Map.Entry<String, String> entry : impacts.entrySet()) {
            TableRow row = new TableRow(requireContext());
            row.setPadding(8, 8, 8, 8);

            TextView stageText = createTableTextView(entry.getKey(), Gravity.START);
            TextView impactText = createTableTextView(entry.getValue(), Gravity.END);

            row.addView(stageText);
            row.addView(impactText);
            table.addView(row);
        }
    }


    private TextView createTableTextView(String text, int gravity) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextColor(Color.parseColor("#666666"));
        textView.setGravity(gravity);
        if (gravity == Gravity.END) {
            textView.setPaddingRelative(0, 0, 50, 0);
        }
        return textView;
    }

}
