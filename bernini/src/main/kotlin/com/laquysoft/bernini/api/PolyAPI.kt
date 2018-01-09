package com.laquysoft.bernini.api

import com.laquysoft.bernini.model.AssetModel
import com.laquysoft.bernini.model.ListAssetsResponse
import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody

/**
 * Created by joaobiriba on 09/01/2018.
 */
interface PolyAPI {
    fun getAsset(assetId: String) : Deferred<AssetModel>

    fun downloadFile(filePath: String): Deferred<ResponseBody>

    fun listAssets(keywords: String) : Deferred<ListAssetsResponse>}