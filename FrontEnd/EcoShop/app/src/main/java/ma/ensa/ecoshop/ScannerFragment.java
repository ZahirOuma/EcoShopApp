package ma.ensa.ecoshop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ma.ensa.ecoshop.databinding.FragmentScannerBinding;
import ma.ensa.ecoshop.model.Product;
import ma.ensa.ecoshop.model.ProductResponse;
import ma.ensa.ecoshop.network.ProductApiService;
import ma.ensa.ecoshop.network.RetrofitClient;
import ma.ensa.ecoshop.service.ProductViewModel;
import ma.ensa.ecoshop.service.ProductViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {
    private static final String TAG = "ScannerFragment";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private FragmentScannerBinding binding;
    private ProductViewModel productViewModel;
    private ProductApiService apiService;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;
    private boolean isProcessingBarcode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        setupViews();
    }

    private void initializeComponents() {
        apiService = RetrofitClient.getInstance().create(ProductApiService.class);
        productViewModel = new ViewModelProvider(this,
                new ProductViewModelFactory(requireContext())).get(ProductViewModel.class);
    }


    private void setupViews() {
        setupBottomSheet();
        initBarcodeScanner();
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.productInfoSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    startProductDetailActivity();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Animation pendant le glissement si nécessaire
            }
        });
    }

    private void initBarcodeScanner() {
        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1080)
                .build();

        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCameraWithPermissionCheck();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            }
        });

        setupBarcodeProcessor();
    }

    private void setupBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0 && !isProcessingBarcode) {
                    isProcessingBarcode = true;
                    String barcode = barcodes.valueAt(0).displayValue;
                    requireActivity().runOnUiThread(() -> handleScannedBarcode(barcode));
                }
            }
        });
    }

    private void startCameraWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    private void startCamera() {
        try {
            if (cameraSource != null) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(binding.surfaceView.getHolder());
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors du démarrage de la caméra: " + e.getMessage());
            showError("Erreur lors du démarrage de la caméra");
        }
    }

    private void handleScannedBarcode(String barcode) {
        searchProduct(barcode);
    }

    private void searchProduct(String barcode) {
        if (barcode.isEmpty()) {
            Log.w(TAG, "searchProduct: Tentative de recherche avec un code-barres vide");
            return;
        }

        Log.d(TAG, "searchProduct: Démarrage de la recherche pour le code-barres: " + barcode);
        setLoading(true);

        apiService.getProductInfo(barcode).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                if (!isAdded()) return;

                setLoading(false);
                isProcessingBarcode = false;

                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    productViewModel.saveProduct(productResponse, barcode)
                            .observe(getViewLifecycleOwner(), product -> {
                                if (product != null && getContext() != null) {
                                    // Chercher le produit dans Room avec le code-barres
                                    productViewModel.getProductByBarcode(barcode)
                                            .observe(getViewLifecycleOwner(), savedProduct -> {
                                                if (savedProduct != null) {
                                                    // Afficher les informations du produit récupéré
                                                    displayProductInfo(savedProduct);
                                                    showBottomSheet();

                                                }
                                            });
                                }
                            });
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;

                Log.e(TAG, "onFailure: Échec de la requête réseau", t);
                isProcessingBarcode = false;
                setLoading(false);
                showError("Erreur de connexion: " + t.getMessage());
            }
        });
    }

    private void displayProductInfo(Product product) {
        if (product != null && getContext() != null) {
            // Récupérer les références des vues
            View productInfoView = binding.productInfoSheet.findViewById(R.id.productInfoSheet);
            ImageView productImage = productInfoView.findViewById(R.id.productImage);
            TextView productName = productInfoView.findViewById(R.id.productName);
            TextView brandName = productInfoView.findViewById(R.id.brandName);
            TextView quantity = productInfoView.findViewById(R.id.quantity);

            // Nouvelle référence pour les scores
            ImageView nutriScoreImage = productInfoView.findViewById(R.id.nutriScoreImage);
            TextView nutriScoreQuality = productInfoView.findViewById(R.id.nutriScoreQuality);
            ImageView ecoScoreImage = productInfoView.findViewById(R.id.ecoScoreImage);
            TextView ecoScoreQuality = productInfoView.findViewById(R.id.ecoScoreQuality);

            // Afficher les informations du produit
            Product.ProductDetails details = product.getProductDetails();
            if (details != null) {
                productName.setText(details.getProductName());
                brandName.setText(details.getBrands());
                quantity.setText(details.getQuantity());
            }

            // Gestion du Nutri-Score
            // Gestion du Nutri-Score
            if (product.getHealthInfo() != null && product.getHealthInfo().getNutriscore() != null) {
                String nutriscoreGrade = product.getHealthInfo().getNutriscore().getGrade();
                String nutriscoreQualityText = product.getHealthInfo().getNutriscore().getQuality();

                // Vérification de la présence de "Nutri-Score"
                if (nutriscoreGrade != null && nutriscoreGrade.startsWith("Nutri-Score ")) {
                    // Extraire la lettre (exemple : "Nutri-Score C" -> 'C')
                    char scoreChar = nutriscoreGrade.charAt(12); // Index fixe pour récupérer la lettre
                    // Générer l'identifiant de l'image drawable
                    int nutriScoreDrawableId = getResources().getIdentifier(
                            String.valueOf(scoreChar).toLowerCase() + "_nutri",
                            "drawable",
                            getContext().getPackageName()
                    );
                    // Vérifier si une ressource est trouvée et l'afficher
                    if (nutriScoreDrawableId != 0) {
                        nutriScoreImage.setImageResource(nutriScoreDrawableId);
                    }
                }

                // Afficher la qualité associée au Nutri-Score
                nutriScoreQuality.setText(nutriscoreQualityText != null ? nutriscoreQualityText : "Qualité inconnue");
            }


            // Gestion de l'Eco-Score
            if (product.getEnvironmentalImpact() != null) {
                String greenScore = product.getEnvironmentalImpact().getGreenScore();
                if (greenScore != null) {
                    // Extraire la lettre principale du score (A, B, C, etc.) à l'aide d'une regex
                    String scoreLetter = null;
                    Pattern pattern = Pattern.compile("([A-E])"); // Recherche une lettre de A à E
                    Matcher matcher = pattern.matcher(greenScore);
                    if (matcher.find()) {
                        scoreLetter = matcher.group(1); // La première lettre correspondant au score
                    }

                    // Ajouter une description basée sur la lettre du score
                    String environmentalDescription = "";
                    if (scoreLetter != null) {
                        switch (scoreLetter) {
                            case "A":
                                environmentalDescription = "Très faible impact environnemental A";
                                break;
                            case "B":
                                environmentalDescription = "Faible impact environnemental B";
                                break;
                            case "C":
                                environmentalDescription = "Impact modéré sur l'environnement C";
                                break;
                            case "D":
                                environmentalDescription = "Impact environnemental élevé D";
                                break;
                            case "E":
                                environmentalDescription = "Impact environnemental très élevé E";
                                break;
                            default:
                                environmentalDescription = "Impact environnemental inconnu";
                                break;
                        }

                        // Ajouter une logique pour l'ID drawable
                        int ecoScoreDrawableId = getResources().getIdentifier(
                                scoreLetter.toLowerCase() + "_eco", // Exemple: "a_eco"
                                "drawable",
                                getContext().getPackageName()
                        );

                        // Si le drawable existe, on l'applique
                        if (ecoScoreDrawableId != 0) {
                            ecoScoreImage.setImageResource(ecoScoreDrawableId);
                        }

                        // Afficher la description dans une vue
                        ecoScoreQuality.setText(environmentalDescription);
                    } else {
                        // Si aucune lettre n'a été trouvée, afficher un message par défaut
                        ecoScoreQuality.setText("Impact environnemental non disponible");
                    }
                }
            }




            // Charger l'image du produit avec Glide
            Product.ProductImage imageInfo = product.getProductImage();
            if (imageInfo != null && imageInfo.getImageUrl() != null) {
                Glide.with(getContext())
                        .load(imageInfo.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(productImage);
            }




            // Afficher la bottom sheet et cacher la carte d'info
            binding.productInfoSheet.setVisibility(View.VISIBLE);
            binding.infoCard.setVisibility(View.GONE);

            Button moreInfoButton = productInfoView.findViewById(R.id.moreInfoButton);

            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Récupérer le code-barres du produit
                    String barcode = product.getBarcode(); // Assurez-vous que cette méthode existe

                    // Créer le bundle avec le code-barres
                    Bundle bundle = new Bundle();
                    bundle.putString("barcode", barcode);

                    // Créer l'instance de ProductDetailsFragment
                    ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
                    detailsFragment.setArguments(bundle);

                    // Naviguer vers le fragment
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, detailsFragment) // Remplacez R.id.fragment_container par votre containerId
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });


        }
    }    private void showBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void handleErrorResponse(Response<ProductResponse> response) {
        String errorMessage = "Erreur lors de la récupération des données (" + response.code() + ")";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (IOException e) {
                Log.e(TAG, "handleErrorResponse: Erreur lors de la lecture du corps d'erreur", e);
            }
        }
        showError(errorMessage);
    }

    private void setLoading(boolean isLoading) {
        // Supprimer ou commenter la ligne qui cache la carte
        // binding.infoCard.setVisibility(isLoading ? View.GONE : View.VISIBLE);

        // Optionnellement, vous pouvez ajouter un indicateur de chargement sans cacher la carte
        // Par exemple, ajouter un ProgressBar dans votre layout et le contrôler ici :
        if (binding != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }



    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                showError("Permission de caméra refusée");
            }
        }
    }

    private void startProductDetailActivity() {
        // Implement the logic to start the product detail activity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraSource != null) {
            cameraSource.stop();
            cameraSource.release();
        }
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
        binding = null;
    }
}