package ma.ensa.ecoshop.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ProductResponse {

    @SerializedName("carbon_footprint")
    private CarbonFootprint carbonFootprint;

    @SerializedName("environmental_impact")
    private EnvironmentalImpact environmentalImpact;

    @SerializedName("health_info")
    private HealthInfo healthInfo;

    @SerializedName("product_details")
    private ProductDetails productDetails;

    @SerializedName("product_image")
    private ProductImage productImage;

    // Getters and Setters
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
        @SerializedName("co2")
        private String co2;

        @SerializedName("description")
        private String description;

        @SerializedName("impacts_by_stage")
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
        @SerializedName("green_score")
        private String greenScore;

        @SerializedName("impacts_by_stage")
        private Map<String, String> impactsByStage;

        @SerializedName("li_texts")
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
        @SerializedName("negative_points")
        private Points negativePoints;

        @SerializedName("nutriscore")
        private Nutriscore nutriscore;

        @SerializedName("positive_points")
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
            @SerializedName("grade")
            private String grade;

            @SerializedName("quality")
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
        @SerializedName("title")
        private String title;

        @SerializedName("components")
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
            @SerializedName("name")
            private String name;

            @SerializedName("value")
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
        @SerializedName("brands")
        private String brands;

        @SerializedName("categories")
        private String categories;

        @SerializedName("ingredients")
        private String ingredients;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("quantity")
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
        @SerializedName("image_url")
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
