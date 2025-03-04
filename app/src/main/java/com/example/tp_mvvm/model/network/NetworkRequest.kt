package com.example.tp_mvvm.model.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun getUnsafeOkHttpClient(): OkHttpClient {
    val x509TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    val trustManagers = arrayOf<TrustManager>(x509TrustManager)

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustManagers, null)

    val builder = OkHttpClient.Builder()
    builder.sslSocketFactory(sslContext.socketFactory, x509TrustManager)
    builder.hostnameVerifier { _, _ -> true}

    return builder.build()
}

private const val BASE_URL = "https://10.0.2.2:5000/" // TODO - replace with server domain address

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        Json.asConverterFactory(
        "application/json".toMediaType())
    )
    .baseUrl(BASE_URL)
    .client(getUnsafeOkHttpClient()) //TODO - remove when SSL certificate is set up or add your own SSL when server is ready
    .build()

interface MedApiService {
    @GET("/")
    suspend fun testApi(): String

    @POST("question")
    suspend fun askModel(@Body body: Map<String, String>): String
}

object MedApi {
    val retrofitService: MedApiService by lazy {
        retrofit.create(MedApiService::class.java)
    }
}
