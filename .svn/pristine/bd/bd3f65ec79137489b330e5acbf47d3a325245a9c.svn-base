package com.facewarrant.fw.net;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Alex on 2017/8/10.
 * Email:2856992713@qq.com
 * Retrofit封装
 */
public class RetrofitManager {
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private String mBaseUrl;
    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private static final long DEFAULT_TIMEOUT = 10000;

    private RetrofitManager(Builder builder) {
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mReadTimeout = builder.mReadTimeout;
        this.mWriteTimeout = builder.mWriteTimeout;
        this.mBaseUrl = builder.mBaseUrl;
        initRetrofit();
    }


    private void initRetrofit() {

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .writeTimeout(mReadTimeout, TimeUnit.SECONDS)
                .readTimeout(mWriteTimeout, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    public <T> T createRequest(Class<T> requestClass) {
        return mRetrofit.create(requestClass);
    }

    public static final class Builder {
        private String mBaseUrl;
        private long mConnectTimeout;
        private long mReadTimeout;
        private long mWriteTimeout;

        public Builder() {
            mConnectTimeout = DEFAULT_TIMEOUT;
            mReadTimeout = DEFAULT_TIMEOUT;
            mWriteTimeout = DEFAULT_TIMEOUT;
        }

        public Builder baseUrl(String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        public Builder connectTimeout(long timeout) {
            mConnectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(long timeout) {
            mReadTimeout = timeout;
            return this;
        }

        public Builder writeTimeout(long timeout) {
            mWriteTimeout = timeout;
            return this;
        }

        public RetrofitManager build() {
            return new RetrofitManager(this);
        }
    }

}
