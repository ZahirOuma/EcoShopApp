package ma.ensa.ecoshop.service;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.ensa.ecoshop.dao.ProductDaoImpl;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.model.ProductResponse;

public class ProductService {
    private ProductDaoImpl productDaoImpl;



    public ProductService(Context context) {
        // Initialiser ProductDaoImpl avec le contexte
        this.productDaoImpl = new ProductDaoImpl(context);
    }

    public void saveProductToDatabase(ProductResponse productResponse, String barcode) {
        if (productResponse == null || barcode == null || barcode.trim().isEmpty()) {
            return;
        }
        Product existingProduct = productDaoImpl.getProductByBarcode(barcode.trim());
        if (existingProduct != null) {
            // Mettre à jour le temps de scan si le produit existe déjà
            existingProduct.setScanTime(new Date()); // Mettre à jour le temps de scan avec la date actuelle
            productDaoImpl.updateProduct(existingProduct); // Mettre à jour le produit dans la base de données
            return;
        }

        // Créer un nouvel objet Product
        Product product = new Product();

        // Définir le code-barres et le temps de scan
        product.setBarcode(barcode.trim());
        product.setScanTime(new Date());

        // Convertir ProductDetails
        if (productResponse.getProductDetails() != null) {
            Product.ProductDetails details = new Product.ProductDetails();
            details.setProductName(productResponse.getProductDetails().getProductName());
            details.setQuantity(productResponse.getProductDetails().getQuantity());
            details.setBrands(productResponse.getProductDetails().getBrands());
            details.setCategories(productResponse.getProductDetails().getCategories());
            details.setIngredients(productResponse.getProductDetails().getIngredients());
            product.setProductDetails(details);
        }

        // Convertir CarbonFootprint
        if (productResponse.getCarbonFootprint() != null) {
            Product.CarbonFootprint carbonFootprint = new Product.CarbonFootprint();
            carbonFootprint.setCo2(productResponse.getCarbonFootprint().getCo2());
            carbonFootprint.setDescription(productResponse.getCarbonFootprint().getDescription());
            carbonFootprint.setImpactsByStage(productResponse.getCarbonFootprint().getImpactsByStage());
            product.setCarbonFootprint(carbonFootprint);
        }

        // Convertir EnvironmentalImpact
        if (productResponse.getEnvironmentalImpact() != null) {
            Product.EnvironmentalImpact environmentalImpact = new Product.EnvironmentalImpact();
            environmentalImpact.setGreenScore(productResponse.getEnvironmentalImpact().getGreenScore());
            environmentalImpact.setLiTexts(productResponse.getEnvironmentalImpact().getLiTexts());
            environmentalImpact.setImpactsByStage(productResponse.getEnvironmentalImpact().getImpactsByStage());
            product.setEnvironmentalImpact(environmentalImpact);
        }

        // Convertir HealthInfo
        if (productResponse.getHealthInfo() != null) {
            Product.HealthInfo healthInfo = new Product.HealthInfo();

            // Conversion des points négatifs
            if (productResponse.getHealthInfo().getNegativePoints() != null) {
                Product.Points negativePoints = new Product.Points();
                negativePoints.setTitle(productResponse.getHealthInfo().getNegativePoints().getTitle());
                negativePoints.setComponents(convertComponents(productResponse.getHealthInfo().getNegativePoints().getComponents()));
                healthInfo.setNegativePoints(negativePoints);
            }

            // Conversion des points positifs
            if (productResponse.getHealthInfo().getPositivePoints() != null) {
                Product.Points positivePoints = new Product.Points();
                positivePoints.setTitle(productResponse.getHealthInfo().getPositivePoints().getTitle());
                positivePoints.setComponents(convertComponents(productResponse.getHealthInfo().getPositivePoints().getComponents()));
                healthInfo.setPositivePoints(positivePoints);
            }

            // Conversion du Nutriscore (votre code existant)
            if (productResponse.getHealthInfo().getNutriscore() != null) {
                Product.HealthInfo.Nutriscore nutriscore = new Product.HealthInfo.Nutriscore();
                nutriscore.setGrade(productResponse.getHealthInfo().getNutriscore().getGrade());
                nutriscore.setQuality(productResponse.getHealthInfo().getNutriscore().getQuality());
                healthInfo.setNutriscore(nutriscore);
            }

            product.setHealthInfo(healthInfo);
        }
        // Convertir ProductImage
        if (productResponse.getProductImage() != null) {
            Product.ProductImage productImage = new Product.ProductImage();
            productImage.setImageUrl(productResponse.getProductImage().getImageUrl());
            product.setProductImage(productImage);
        }

        // Sauvegarder dans la base de données
        productDaoImpl.insertProduct(product);
    }

    public Product getProductByBarcode(String barcode) {
        // Logique pour récupérer le produit en fonction du code-barres depuis la base de données
        return productDaoImpl.getProductByBarcode(barcode);
    }

    private List<Product.Points.Component> convertComponents(List<ProductResponse.Points.Component> components) {
        if (components == null) return null;
        List<Product.Points.Component> result = new ArrayList<>();
        for (ProductResponse.Points.Component comp : components) {
            Product.Points.Component newComp = new Product.Points.Component();
            newComp.setName(comp.getName());
            newComp.setValue(comp.getValue());
            result.add(newComp);
        }
        return result;
    }


    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();
        new Thread(() -> {
            List<Product> products = productDaoImpl.getAllProducts();
            data.postValue(products);
        }).start();
        return data;
    }

    public void deleteProduct(Product product) {
        productDaoImpl.deleteProduct(product);
    }




}
