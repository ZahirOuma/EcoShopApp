package ma.ensa.ecoshop.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Embedded;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ma.ensa.ecoshop.utils.ComponentConverter;
import ma.ensa.ecoshop.utils.DateConverter;
import ma.ensa.ecoshop.utils.ListConverter;
import ma.ensa.ecoshop.utils.MapConverter;

@Entity(tableName = "product")
@TypeConverters({ComponentConverter.class, MapConverter.class, ListConverter.class, DateConverter.class})
public class Product  {

    @PrimaryKey(autoGenerate = true)
    private int id; // ID généré automatiquement pour Room

    @ColumnInfo(name = "barcode")  // Champ pour le code-barres
    private String barcode; // Code-barres du produit

    @ColumnInfo(name = "scan_time")  // Champ pour le temps de scan
    private Date scanTime; // Temps de scan en tant que date


    @Embedded
    private CarbonFootprint carbonFootprint;

    @Embedded
    private EnvironmentalImpact environmentalImpact;

    @Embedded
    private HealthInfo healthInfo;

    @Embedded
    private ProductDetails productDetails;

    @Embedded
    private ProductImage productImage;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getScanTime() {
        return scanTime;
    }

    public void setScanTime(Date scanTime) {
        this.scanTime = scanTime;
    }

    public CarbonFootprint getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(CarbonFootprint carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public EnvironmentalImpact getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public void setEnvironmentalImpact(EnvironmentalImpact environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public HealthInfo getHealthInfo() {
        return healthInfo;
    }

    public void setHealthInfo(HealthInfo healthInfo) {
        this.healthInfo = healthInfo;
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDetails productDetails) {
        this.productDetails = productDetails;
    }

    public ProductImage getProductImage() {
        return productImage;
    }

    public void setProductImage(ProductImage productImage) {
        this.productImage = productImage;
    }


    // === Classes internes ===

    public static class CarbonFootprint {
        private String co2;
        private String description;

        @ColumnInfo(name = "impactsByStage_carbonFootprint")
        @TypeConverters(MapConverter.class)
        private Map<String, String> impactsByStage;





        // Getters and Setters
        public String getCo2() {
            return co2;
        }

        public void setCo2(String co2) {
            this.co2 = co2;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, String> getImpactsByStage() {
            return impactsByStage;
        }

        public void setImpactsByStage(Map<String, String> impactsByStage) {
            this.impactsByStage = impactsByStage;
        }
    }

    public static class EnvironmentalImpact {
        private String greenScore;

        @ColumnInfo(name = "impactsByStage_environmentalImpact")
        @TypeConverters(MapConverter.class)
        private Map<String, String> impactsByStage;

        @TypeConverters(ListConverter.class)
        private List<String> liTexts;

        // Getters and Setters
        public String getGreenScore() {
            return greenScore;
        }

        public void setGreenScore(String greenScore) {
            this.greenScore = greenScore;
        }

        public Map<String, String> getImpactsByStage() {
            return impactsByStage;
        }

        public void setImpactsByStage(Map<String, String> impactsByStage) {
            this.impactsByStage = impactsByStage;
        }

        public List<String> getLiTexts() {
            return liTexts;
        }

        public void setLiTexts(List<String> liTexts) {
            this.liTexts = liTexts;
        }
    }

    public static class HealthInfo {
        @Embedded(prefix = "negative_")
        private Points negativePoints;

        @Embedded(prefix = "nutriscore_")
        private Nutriscore nutriscore;

        @Embedded(prefix = "positive_")
        private Points positivePoints;

        // Getters and Setters
        public Points getNegativePoints() {
            return negativePoints;
        }

        public void setNegativePoints(Points negativePoints) {
            this.negativePoints = negativePoints;
        }

        public Nutriscore getNutriscore() {
            return nutriscore;
        }

        public void setNutriscore(Nutriscore nutriscore) {
            this.nutriscore = nutriscore;
        }

        public Points getPositivePoints() {
            return positivePoints;
        }

        public void setPositivePoints(Points positivePoints) {
            this.positivePoints = positivePoints;
        }

        public static class Nutriscore {
            private String grade;
            private String quality;

            // Getters and Setters
            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }
        }
    }

    public static class Points {
        private String title;

        @TypeConverters(ComponentConverter.class)
        private List<Component> components;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Component> getComponents() {
            return components;
        }

        public void setComponents(List<Component> components) {
            this.components = components;
        }

        public static class Component {
            private String name;
            private String value;

            // Getters and Setters
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

    public static class ProductDetails {
        private String brands;
        private String categories;
        private String ingredients;
        private String productName;
        private String quantity;

        // Getters and Setters
        public String getBrands() {
            return brands;
        }

        public void setBrands(String brands) {
            this.brands = brands;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getIngredients() {
            return ingredients;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }

    public static class ProductImage {
        private String imageUrl;

        // Getter and Setter
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}