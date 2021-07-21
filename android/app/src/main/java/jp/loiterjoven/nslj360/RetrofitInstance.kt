package jp.loiterjoven.nslj360

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitInstance {
    companion object {
        val retrofit: ApiService by lazy {
            val httpClient = OkHttpClient.Builder()
            val builder = Retrofit.Builder()
                .baseUrl(Constant.BE_SERVER_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}