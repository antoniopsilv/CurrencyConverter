package br.edu.ifsp.scl.sdm.currencyconverter.model.api

import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.ConversionResult
import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.CurrencyList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CurrencyConverterApiService {
    @Headers(
        "x-rapiapi-host: currency-converter5.p.rapidapi.com",
        "x-rapidapi-key: ceca8c7820mshc0919944ac028f2p1451edjsn36a0a403afb5"
    )
    @GET("list")
    fun getCurricies(): Call<CurrencyList>

    @Headers(
        "x-rapiapi-host: currency-converter5.p.rapidapi.com",
        "x-rapidapi-key: ceca8c7820mshc0919944ac028f2p1451edjsn36a0a403afb5"
    )
    @GET("convert")
    fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String
    ): Call<ConversionResult>
}