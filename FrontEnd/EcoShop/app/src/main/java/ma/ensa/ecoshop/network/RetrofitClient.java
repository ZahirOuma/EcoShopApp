package ma.ensa.ecoshop.network;

import ma.ensa.ecoshop.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static volatile Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    // Add logging interceptor
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // Configure OkHttpClient
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .build();

                    // Build Retrofit instance
                    instance = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return instance;
    }
}
