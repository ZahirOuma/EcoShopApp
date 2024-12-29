package ma.ensa.ecoshop.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ma.ensa.ecoshop.model.Product;

@Dao
public interface ProductDao {

    // Insérer un produit
    @Insert
    void insertProduct(Product product);

    // Insérer une liste de produits
    @Insert
    void insertProducts(List<Product> products);

    // Mettre à jour un produit
    @Update
    void updateProduct(Product product);

    // Supprimer un produit
    @Delete
    void deleteProduct(Product product);

    // Supprimer tous les produits
    @Query("DELETE FROM product")
    void deleteAllProducts();

    // Récupérer tous les produits
    @Query("SELECT * FROM product")
    List<Product> getAllProducts();

    // Récupérer un produit par son ID
    @Query("SELECT * FROM product WHERE id = :productId")
    Product getProductById(int productId);

    // Rechercher des produits par nom
    @Query("SELECT * FROM product WHERE productName LIKE '%' || :name || '%'")
    List<Product> findProductsByName(String name);

    // Récupérer un produit par son code-barre
    @Query("SELECT * FROM product WHERE barcode = :barcode")
    Product getProductByBarcode(String barcode);


}