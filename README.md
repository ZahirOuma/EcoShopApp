## EcoShop Application Architecture ##

# Table of Contents
1. Software Architecture
2. Docker Configuration
3. Frontend Structure
4. Backend Structure
5. Mobile Structure
6. Getting Started
7. Video Demonstration

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
|
|-- adapter: RecyclerView adapters for scanned product lists
|-- api: Retrofit API client and services
|-- database: Room database setup and DAOs
|-- model: Data models (Product, User)
|-- repository: Handles API calls and database operations
|-- viewmodel: Manages UI-related data for fragments
|-- views:
    |-- MainActivity.java
    |-- fragments:
        |-- ScannerFragment.java
        |-- ListFragment.java
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
|-- routes.py  # API routes for product scanning
|-- models.py  # Data models
|-- scraper.py  # Web scraping logic for Open Food Facts
|-- requirements.txt
```

**API Endpoints:**

```python
from flask import Flask, jsonify, request
from scraper import scrape_product_details

app = Flask(__name__)

@app.route('/api/scan', methods=['POST'])
def scan():
    barcode = request.json.get('barcode')
    product_details = scrape_product_details(barcode)
    if product_details:
        return jsonify(product_details), 200
    return jsonify({'error': 'Product not found'}), 404

if __name__ == '__main__':
    app.run(debug=True)
```

---

### Getting Started

#### Backend Setup:
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd backend
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Run the Flask server:
   ```bash
   python app.py
   ```

#### Mobile Setup:
1. Open the Android project in Android Studio.
2. Update the API endpoint in `ApiClient.java`:
   ```java
   public static final String BASE_URL = "http://<your-backend-url>:5000/api/";
   ```
3. Build and run the app on an emulator or physical device.

---

### Video Demonstration
- **Link to demonstration video:** `<add-your-link>`

---

### Contributing
We welcome contributions! Please fork the repository and submit a pull request with your changes.

### Contributors:
- **Your Name (GitHub)**
- **Your Team Members (GitHub)**
