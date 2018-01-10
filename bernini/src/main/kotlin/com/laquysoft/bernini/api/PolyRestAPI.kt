package com.laquysoft.bernini.api

import com.laquysoft.bernini.model.AssetModel
import com.laquysoft.bernini.model.ListAssetsResponse
import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Created by joaobiriba on 09/01/2018.
 */
class PolyRestAPI @Inject constructor(private val polyService: PolyService) : PolyAPI {

    var apiKey: String = "not set"

    override fun getAsset(assetId: String): Deferred<AssetModel> {
        return polyService.getAsset(assetId, apiKey)
    }

    override fun downloadFile(filePath: String): Deferred<ResponseBody> {
        return polyService.downloadFile(filePath, apiKey)
    }

    override fun listAssets(keywords: String): Deferred<ListAssetsResponse> {
        return polyService.listAssets(apiKey, keywords)
    }

}