import com.laquysoft.bernini.model.AssetModel
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Created by joaobiriba on 09/01/2018.
 */
class BerniniTest {

//    var apiMock = mock<PolyAPI>()
//
//    @Before
//    fun setup() {
//        apiMock = mock<PolyAPI>()
//    }
//
//    @Test
//    fun testSuccess_getModel() {
//
//        launch {
//            val drawOrder = async {
//                bernini.getModel(ASSET_ID)
//            }
//            resourcesList = drawOrder.await()
//        }
//
//
//        // prepare
//        val assetModel : AssetModel = AssetModel("aModel", "aDisplayName", "anAuthor",
//                listOf())
//        val response = Response.success(assetModel)
//
//        when(callMock.execute()).thenReturn(response)
//
//        // call
//        val newsManager = NewsManager(apiMock)
//        newsManager.getNews("").subscribe(testSub)
//
//        // assert
//        testSub.assertNoErrors()
//        testSub.assertValueCount(1)
//        testSub.assertCompleted()
//    }
}