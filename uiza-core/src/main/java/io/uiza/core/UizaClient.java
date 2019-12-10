package io.uiza.core;

import android.content.Context;
import android.text.TextUtils;

import com.squareup.moshi.Moshi;

import java.security.InvalidParameterException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.uiza.core.api.UizaV3Service;
import io.uiza.core.api.UizaV5Service;
import io.uiza.core.deserializers.DateTimeAdapter;
import io.uiza.core.interceptors.GzipRequestInterceptor;
import io.uiza.core.interceptors.RestRequestInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

public class UizaClient {
    private static final int CONNECT_TIMEOUT_TIME = 20;//20s
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static Retrofit retrofit;
    private static RestRequestInterceptor restRequestInterceptor;
    private static final int CACHE_SIZE = 10; // 10M

    public static class Builder {
        Context context;
        String baseApiUrl;
        String token;
        long timeout = CONNECT_TIMEOUT_TIME;
        int cacheSize = CACHE_SIZE;
        boolean retryOnConnectionFailure = true;
        boolean compressedRequest = false;

        public Builder(@NonNull Context context, @NonNull String baseApiUrl) {
            this.context = context;
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
        public Builder withTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * connect and read timeout
         * Default 10 MB
         *
         * @param cacheSize (MB)
         */
        public Builder withCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
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
            return new UizaClient(context, baseApiUrl, token, timeout, cacheSize,
                    retryOnConnectionFailure, compressedRequest);
        }
    }

    private UizaClient(Context context, String baseApiUrl,
                       String token,
                       long timeout,
                       int cacheSize,
                       boolean retryOnConnectionFailure,
                       boolean compressedRequest
    ) {
        // config Logger
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
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        restRequestInterceptor = new RestRequestInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(restRequestInterceptor)
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .cache(new Cache(context.getCacheDir(), cacheSize * 1024 * 1024)) // <-- cacheSize MB
                .addInterceptor(logging);  // <-- this is the important line!

        if (compressedRequest)
            builder.addInterceptor(new GzipRequestInterceptor());

        final OkHttpClient okHttpClient = builder.build();
        Moshi moshi = new Moshi.Builder().add(new DateTimeAdapter()).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseApiUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();

        if (!TextUtils.isEmpty(token)) {
            addAuthorization(token);
        }
        addHeader(CONTENT_TYPE, "application/json");
    }

    private <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            throw new IllegalStateException("Must call init() before using");
        }
        return retrofit.create(serviceClass);
    }

    public UizaV5Service createLiveV5Service() {
        return createService(UizaV5Service.class);
    }

    public UizaV3Service createLiveV3Service() {
        return createService(UizaV3Service.class);
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
