package ma.ensa.ecoshop.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ma.ensa.ecoshop.repository.ProductRepository;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.model.ProductResponse;

public class ProductViewModel extends ViewModel {
    private ProductRepository productRepository;

    public ProductViewModel(Context context) {
        productRepository = new ProductRepository(context);
    }

    public LiveData<Product> saveProduct(ProductResponse productResponse, String barcode) {
        return productRepository.saveProductToDatabase(productResponse, barcode);
    }

    public LiveData<Product> getProductByBarcode(String barcode) {
        return productRepository.getProductByBarcode(barcode);
    }

    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public void deleteProduct(Product product) {
        productRepository.deleteProduct(product);
    }

}