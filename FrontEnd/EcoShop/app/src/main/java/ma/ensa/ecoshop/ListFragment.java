package ma.ensa.ecoshop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.service.ProductViewModel;
import ma.ensa.ecoshop.service.ProductViewModelFactory;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductViewModel productViewModel;
    private List<Product> productList = new ArrayList<>();
    private ColorDrawable swipeBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize swipe background
        swipeBackground = new ColorDrawable(Color.RED);

        ProductViewModelFactory factory = new ProductViewModelFactory(requireContext());
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        recyclerView.setAdapter(new ProductAdapter());

        setupSwipeToDelete();

        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            productList = products;
            recyclerView.getAdapter().notifyDataSetChanged();
        });

        return view;
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product product = productList.get(position);
                productViewModel.deleteProduct(product);
                productList.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (dX < 0) { // Swiping to the left
                    swipeBackground.setBounds(itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                }

                swipeBackground.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    // Rest of your code remains the same (ProductAdapter and ProductViewHolder)
    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            private final ImageView productImage;
            private final TextView productName;
            private final TextView brandName;
            private final ImageView nutriScoreImage;
            private final ImageView ecoScoreImage;

            ProductViewHolder(View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                productName = itemView.findViewById(R.id.productName);
                brandName = itemView.findViewById(R.id.brandName);
                nutriScoreImage = itemView.findViewById(R.id.nutriScoreImage);
                ecoScoreImage = itemView.findViewById(R.id.ecoScoreImage);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product product = productList.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("barcode", product.getBarcode());

                        ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
                        detailsFragment.setArguments(bundle);

                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, detailsFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
            }

            void bind(Product product) {
                if (product.getProductDetails() != null) {
                    productName.setText(product.getProductDetails().getProductName());
                    brandName.setText(product.getProductDetails().getBrands());
                }

                if (product.getProductImage() != null && product.getProductImage().getImageUrl() != null) {
                    Glide.with(itemView.getContext())
                            .load(product.getProductImage().getImageUrl())
                            .into(productImage);
                }

                // Nutri-Score setup
                if (product.getHealthInfo() != null &&
                        product.getHealthInfo().getNutriscore() != null &&
                        product.getHealthInfo().getNutriscore().getGrade() != null) {

                    String nutriscoreGrade = product.getHealthInfo().getNutriscore().getGrade();
                    if (nutriscoreGrade.startsWith("Nutri-Score ")) {
                        char scoreChar = nutriscoreGrade.charAt(12);
                        int nutriScoreDrawableId = itemView.getContext().getResources().getIdentifier(
                                String.valueOf(scoreChar).toLowerCase() + "_nutri",
                                "drawable",
                                itemView.getContext().getPackageName()
                        );
                        if (nutriScoreDrawableId != 0) {
                            nutriScoreImage.setImageResource(nutriScoreDrawableId);
                        }
                    }
                }

                // Eco-Score setup
                if (product.getEnvironmentalImpact() != null &&
                        product.getEnvironmentalImpact().getGreenScore() != null) {

                    String greenScore = product.getEnvironmentalImpact().getGreenScore();
                    Pattern pattern = Pattern.compile("([A-E])");
                    Matcher matcher = pattern.matcher(greenScore);
                    if (matcher.find()) {
                        String scoreLetter = matcher.group(1);
                        int ecoScoreDrawableId = itemView.getContext().getResources().getIdentifier(
                                scoreLetter.toLowerCase() + "_eco",
                                "drawable",
                                itemView.getContext().getPackageName()
                        );
                        if (ecoScoreDrawableId != 0) {
                            ecoScoreImage.setImageResource(ecoScoreDrawableId);
                        }
                    }
                }
            }
        }
    }
}