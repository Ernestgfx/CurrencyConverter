import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeApi {
    @GET("latest")
    fun getRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") from: String,
        @Query("currencies") to: String
    ): Call<ExchangeResponse>
}


