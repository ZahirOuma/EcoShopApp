package ma.ensa.ecoshop.network;

import ma.ensa.ecoshop.model.ProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/product")
    Call<ProductResponse> getProductInfo(@Query("barcode") String barcode);
}
