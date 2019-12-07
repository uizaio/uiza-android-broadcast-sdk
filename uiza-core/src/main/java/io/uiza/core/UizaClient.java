package io.uiza.core;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.uiza.core.api.UizaLiveV5Service;
import io.uiza.core.deserializers.DateTypeDeserializer;
import io.uiza.core.interceptors.GzipRequestInterceptor;
import io.uiza.core.interceptors.RestRequestInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class UizaClient {
    private static final int CONNECT_TIMEOUT_TIME = 20;//20s
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static Retrofit retrofit;
    private static RestRequestInterceptor restRequestInterceptor;

    public static class Builder {
        String baseApiUrl;
        String token;
        long timeout = CONNECT_TIMEOUT_TIME;
        boolean retryOnConnectionFailure = true;
        boolean compressedRequest = false;

        public Builder(@NonNull String baseApiUrl) {
            this.baseApiUrl = baseApiUrl;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        /**
         * connect and read timeout
         * Default 20s
         *
         * @param timeout (seconds)
         */
        public void withTimeout(long timeout) {
            this.timeout = timeout;
        }

        /**
         * Default true
         *
         * @param retryOnConnectionFailure
         */
        public Builder withRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }

        /**
         * Default false
         *
         * @param compressedRequest
         */
        public Builder withCompressedRequest(boolean compressedRequest) {
            this.compressedRequest = compressedRequest;
            return this;
        }

        public UizaClient builder() {
            if (TextUtils.isEmpty(baseApiUrl)) {
                throw new InvalidParameterException("baseApiUrl cannot null or empty");
            }
            return new UizaClient(baseApiUrl, token, timeout,
                    retryOnConnectionFailure, compressedRequest);
        }
    }

    private UizaClient(String baseApiUrl,
                       String token,
                       long timeout,
                       boolean retryOnConnectionFailure,
                       boolean compressedRequest
    ) {

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.d(message);
            }
        });
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        restRequestInterceptor = new RestRequestInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(restRequestInterceptor)
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .addInterceptor(logging);  // <-- this is the important line!

        if (compressedRequest)
            builder.addInterceptor(new GzipRequestInterceptor());

        final OkHttpClient okHttpClient = builder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseApiUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        if (!TextUtils.isEmpty(token)) {
            addAuthorization(token);
        }
        addHeader(CONTENT_TYPE, "application/json");
    }

    public <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            throw new IllegalStateException("Must call init() before using");
        }
        return retrofit.create(serviceClass);
    }

    public UizaLiveV5Service createLiveV5Service() {
        return createService(UizaLiveV5Service.class);
    }

    public void addHeader(String name, String value) {
        if (restRequestInterceptor != null) {
            restRequestInterceptor.addHeader(name, value);
        }
    }

    public void addAuthorization(String token) {
        addHeader(AUTHORIZATION, token);
    }

    public void removeAuthorization() {
        removeHeader(AUTHORIZATION);
    }

    public void removeHeader(String name) {
        if (restRequestInterceptor != null) {
            restRequestInterceptor.removeHeader(name);
        }
    }

    public boolean hasHeader(String name) {
        if (restRequestInterceptor != null) {
            return restRequestInterceptor.hasHeader(name);
        }
        return false;
    }
}
