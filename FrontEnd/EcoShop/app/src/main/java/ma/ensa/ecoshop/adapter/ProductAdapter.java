package ma.ensa.ecoshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import ma.ensa.ecoshop.R;
import ma.ensa.ecoshop.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = products.get(position);

        if (currentProduct.getProductDetails() != null) {
            holder.productName.setText(currentProduct.getProductDetails().getProductName());
            holder.productBrand.setText(currentProduct.getProductDetails().getBrands());

            // Chargement de l'image avec Glide si une URL d'image est disponible
            if (currentProduct.getProductImage().getImageUrl() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(currentProduct.getProductImage().getImageUrl())
                        .into(holder.productImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public Product getProductAt(int position) {
        return products.get(position);
    }

    public List<Product> getProducts() {
        return products;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productBrand;
        private ImageView productImage;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productBrand = itemView.findViewById(R.id.productBrand);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}