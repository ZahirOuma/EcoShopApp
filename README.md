# EcoShop: Empowering Sustainable Purchasing Decisions

EcoShop is an innovative mobile application designed to empower consumers to make sustainable and informed purchasing decisions. With EcoShop, users can scan product barcodes to instantly access crucial information, including:

- *Nutri-Score*: A nutritional rating to help users make healthier choices.
- *Environmental Impact*: Information on the environmental footprint of the product.
- *Carbon Footprint*: Data on the product's carbon emissions.
- *Detailed Product Information*: Includes the product's name, image, ingredients, and more.
# Table of Contents
1. [Software Architecture](#software-architecture)
2. [Mobile Structure](#mobile-structure)
3. [Backend Structure](#backend-structure)
4. [Getting Started](#getting-started)
    - [Backend Setup](#backend-setup)
    - [Mobile Setup](#mobile-setup)
5. [Video Demonstration](#video-demonstration)
6. [Contributing](#contributing)
7. [Contributors](#contributors)


### Software Architecture
The EcoShop application follows a modern three-tier architecture:


1. **Frontend:** Built using Android Studio, featuring architecture with fragments for Scanner Profile Product Details and List views.
2. **Backend:** Flask-based REST API that handles barcode scanning and retrieves product details using web scraping from Open Food Facts.
3. **Database:** Local storage on mobile devices using Room, with user information and scanned product history stored locally.

---


### Mobile Structure

**Package Organization:**

```plaintext
com.example.ecoshop
|-- adapter: Adapters for managing and displaying lists. 
|-- dao: Data Access Objects for database operations.
|-- data: Room database setup and configuration.
|-- model: Data models (e.g., Product, User).
|-- network: Retrofit API client and services for handling API interactions.
|-- repository: Bridges network and database, handling data operations and business logic.
|-- utils: Utility classes and helpers (e.g., constants, common methods).
|-- views:
    |-- activities:
        |-- SplashActivity.java
        |-- FirstActivity.java
    |-- fragments:
        |-- AppInfoFragment.java
        |-- EcoScoreDetailFragment.java
        |-- ListFragment.java
        |-- NutriScoreDetailFragment.java
        |-- ProductDetailsFragment.java
        |-- ProfileFragment.java
        |-- ScannerFragment.java
        |-- UserFormActivity.java

```

**Dependencies:**

```groovy
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.playServicesVision)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.fragment) 
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.circleimageview)

    
}
```

---

### Backend Structure
The backend is built using Flask with the following structure:

```plaintext
/app
|-- app.py  # Main entry point for the Flask app

```

**API Endpoints:**

- **Description:** Scans a product barcode and fetches its details.
- **Request:**
    ```json
    {
        "barcode": "1234567890123"
    }
    ```
- **Response:**
    - **Success:** Returns the product details in JSON format.
    - **Error:** Returns an error message if the product is not found.

### Getting Started

#### Backend Setup:
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd backend
   ```
2. Install dependencies:
   ```bash
  pip install flask
  pip install selenium
  pip install beautifulsoup4

   ```
3. Run the Flask server:
   ```bash
   flask run --host=0.0.0.0
   ```

#### Mobile Setup:
1. Open the Android project in Android Studio.
2. Update the API endpoint in `Constants.java`:
   ```java
   public static final String BASE_URL = "http://<your-backend-url>:5000/api/";
   ```
3. Build and run the app on an emulator or physical device.

---

### Video Demonstration
- https://github.com/user-attachments/assets/bc57dbb5-bfa2-409e-a717-5a1bdc0ef813


---

### Contributing
We welcome contributions! Please fork the repository and submit a pull request with your changes.

### Contributors:
- **Your Name (GitHub)**
- **Your Team Members (GitHub)**
