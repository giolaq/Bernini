package com.laquysoft.bernini.api

import com.laquysoft.bernini.model.AssetModel
import com.laquysoft.bernini.model.ListAssetsResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.ResponseBody
import retrofit2.http.Query


/**
 * Created by joaobiriba on 22/12/2017.
 */
interface PolyService {

    @GET("/v1/assets/{assetId}/")
    fun getAsset(@Path("assetId") assetId: String, @Query("key") key: String) : Deferred<AssetModel>

    @GET("/{filePath}")
    fun downloadFile(@Path("filePath", encoded = true) filePath: String, @Query("key") key: String): Deferred<ResponseBody>

    @GET("/v1/assets/")
    fun listAssets(@Query("key") key: String, @Query("keywords") keywords: String) : Deferred<ListAssetsResponse>
}