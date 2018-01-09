package com.laquysoft.bernini

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.laquysoft.bernini.api.PolyRestAPI
import com.laquysoft.bernini.api.PolyService
import com.laquysoft.bernini.model.Entry
import com.laquysoft.bernini.model.FileModel
import com.laquysoft.bernini.model.FormatModel
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Created by joaobiriba on 02/01/2018.
 */
class Bernini  {

    private var format: String = "not set"

    var resourcesList: MutableList<Entry> = mutableListOf()
    var assetsList: MutableList<String> = mutableListOf()

    @Inject
    lateinit var api: PolyRestAPI

    fun withApiKey(apiKey: String) = fluently {
        api.apiKey = apiKey
    }

    fun withFormat(format: String) = fluently {
        this.format = format
    }

    private suspend fun requestDataFiles(objFormat: FormatModel) =
            async {
                val rootFile = objFormat.root
                if (rootFile.relativePath.toLowerCase().endsWith(".obj")) {

                    val downloadList = objFormat.resources.toMutableList()
                    downloadList.add(FileModel(rootFile.relativePath, rootFile.url, "OBJ"))

                    val resultList = asyncDownloadList(downloadList)

                    async {
                        while (resultList.count { it.isActive } != 0) {
                            println("Ancora no!")
                        }
                    }.await()

                }
            }

    private fun asyncDownload(fileModel: FileModel) = async {
        val result = api.downloadFile(fileModel.url.drop(28)).await()
        saveFiles(fileModel.relativePath, fileModel.url, result)
        result
    }

    private fun asyncDownloadList(fileModels: List<FileModel>): MutableList<Deferred<ResponseBody>> {
        val downloadList: MutableList<Deferred<ResponseBody>> = mutableListOf()
        fileModels.forEach { fileModel ->
            var responseBody = asyncDownload(fileModel)
            downloadList.add(responseBody)
        }
        return downloadList
    }

    private fun saveFiles(path: String, url: String, content: ResponseBody): Boolean? {
        val responseBody: ByteArray? = content.bytes()
        resourcesList.add(Entry(path, url, responseBody))
        return true
    }

    suspend fun getModel(assetId: String): MutableList<Entry> {
        val assetModel = api.getAsset(assetId).await()
        assetModel.formats.forEach { format -> requestDataFiles(format).await() }
        return resourcesList
    }

    suspend fun listAssets(keywords: String): MutableList<String> {
        val listAssetsResponse = api.listAssets(keywords).await()
        listAssetsResponse.assets.forEach { asset -> assetsList.add(asset.name) }
        return assetsList
    }

}

fun <T : Any> T.fluently(func: () -> Unit): T {
    return this.apply { func() }
}