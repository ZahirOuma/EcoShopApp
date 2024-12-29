package ma.ensa.ecoshop.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ma.ensa.ecoshop.service.ProductService;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.model.ProductResponse;

public class ProductRepository {
    private ProductService productService;
    private MutableLiveData<Product> productLiveData = new MutableLiveData<>();

    public ProductRepository(Context context) {
        productService = new ProductService(context);
    }

    public LiveData<Product> saveProductToDatabase(ProductResponse productResponse, String barcode) {
        // Effectuer le travail dans un thread de fond, par exemple via un Executor ou un autre thread
        new Thread(() -> {
            productService.saveProductToDatabase(productResponse, barcode);
            // Récupérer le produit après l'insertion dans la base de données
            // (vous pourriez avoir besoin de récupérer ce produit à partir de la base de données ici)
            Product product = productService.getProductByBarcode(barcode);
            productLiveData.postValue(product);  // Mettre à jour LiveData
        }).start();

        return productLiveData;
    }

    public LiveData<Product> getProductByBarcode(String barcode) {
        MutableLiveData<Product> result = new MutableLiveData<>();

        // Effectuer la recherche dans un thread séparé
        new Thread(() -> {
            Product product = productService.getProductByBarcode(barcode);
            result.postValue(product);
        }).start();

        return result;
    }

    public LiveData<List<Product>> getAllProducts() {
        return productService.getAllProducts();
    }




    public void deleteProduct(Product product) {
        new Thread(() -> {
            productService.deleteProduct(product);
        }).start();
    }


}