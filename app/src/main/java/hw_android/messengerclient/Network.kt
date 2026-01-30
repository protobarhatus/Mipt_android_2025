package hw_android.messengerclient

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


class NetworkService {

    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("oauth", "0123456789")
            .build()
        chain.proceed(request)
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val apiService = Retrofit.Builder()
        .baseUrl("http://emil-international.ru/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiService::class.java)


}

data class ChatObject(val chats: ArrayList<ChatHeader>) {}

data class ChatHeader (val id: Int, val name : String) {
}
data class Message(val id: Int, val text: String)
data class ChatContent (val id: Int, val messages: ArrayList<Message>) {

}

interface ApiService {
    @GET("mipt_network/chats")
    suspend fun chatsList(): ChatObject

    @GET("mipt_network/chat")
    suspend fun chatContent(@Query("id") id: Int): ChatContent

    @POST("mipt_network/msg")
    suspend fun sendMessage(@Query("id") id: Int, @Query("text") text: String)
    @POST("mipt_network/create_chat")
    suspend fun createChat(@Query("name") name: String)

}