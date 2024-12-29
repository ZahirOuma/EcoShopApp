package ma.ensa.ecoshop.service;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProductViewModelFactory implements ViewModelProvider.Factory {
    private Context context;

    public ProductViewModelFactory(Context context) {
        this.context = context.getApplicationContext();  // Utiliser le context d'application pour éviter les fuites de mémoire
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
