package ma.ensa.ecoshop.dao;

import android.content.Context;

import java.util.List;

import ma.ensa.ecoshop.data.AppDatabase;
import ma.ensa.ecoshop.model.Product;

// This class is responsible for providing the ProductDao instance and interacting with the database
public class ProductDaoImpl {
    private ProductDao productDao;
    private static ProductDaoImpl instance;

    // Private constructor for Singleton pattern
    public ProductDaoImpl(Context context) {
        // Access the AppDatabase instance and get the ProductDao
        AppDatabase db = AppDatabase.getInstance(context);
        productDao = db.productDao();
    }

    // Method to get the unique instance of ProductDaoImpl
    public static synchronized ProductDaoImpl getInstance(Context context) {
        if (instance == null) {
            instance = new ProductDaoImpl(context);
        }
        return instance;
    }

    // Wrapper methods to delegate to ProductDao methods

    public void insertProduct(Product product) {
        try {
            productDao.insertProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting product: " + e.getMessage());
        }
    }

    public void insertProducts(List<Product> products) {
        try {
            productDao.insertProducts(products);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting products: " + e.getMessage());
        }
    }

    public void updateProduct(Product product) {
        try {
            productDao.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating product: " + e.getMessage());
        }
    }

    public void deleteProduct(Product product) {
        try {
            productDao.deleteProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting product: " + e.getMessage());
        }
    }

    public void deleteAllProducts() {
        try {
            productDao.deleteAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting all products: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDao.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching all products: " + e.getMessage());
        }
    }

    public Product getProductById(int productId) {
        try {
            return productDao.getProductById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching product by ID: " + e.getMessage());
        }
    }

    public List<Product> findProductsByName(String name) {
        try {
            return productDao.findProductsByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error searching products by name: " + e.getMessage());
        }
    }

    public Product getProductByBarcode(String barcode) {
        try {
            return productDao.getProductByBarcode(barcode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching product by barcode: " + e.getMessage());
        }
    }
}
