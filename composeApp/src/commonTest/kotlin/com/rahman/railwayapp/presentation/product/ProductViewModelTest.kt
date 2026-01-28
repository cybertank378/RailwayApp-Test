import com.rahman.railwayapp.core.network.NetworkMonitor
import com.rahman.railwayapp.core.sync.SyncManager
import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.domain.usecase.CreateProductUseCase
import com.rahman.railwayapp.domain.usecase.DeleteProductUseCase
import com.rahman.railwayapp.domain.usecase.GetProductDetailUseCase
import com.rahman.railwayapp.domain.usecase.GetProductsUseCase
import com.rahman.railwayapp.domain.usecase.UpdateProductUseCase
import com.rahman.railwayapp.presentation.product.ProductViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals



@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // -------------------- Mocks --------------------
    private val getProductsUseCase = mockk<GetProductsUseCase>()
    private val getProductDetailUseCase = mockk<GetProductDetailUseCase>()
    private val createProductUseCase = mockk<CreateProductUseCase>()
    private val updateProductUseCase = mockk<UpdateProductUseCase>()
    private val deleteProductUseCase = mockk<DeleteProductUseCase>()
    private val networkMonitor = mockk<NetworkMonitor>()
    private val syncManager = mockk<SyncManager>(relaxed = true)

    private lateinit var viewModel: ProductViewModel

    // -------------------- Setup --------------------
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getProductsUseCase.invoke() } returns MutableStateFlow(emptyList())
        every { networkMonitor.observe() } returns MutableStateFlow(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initViewModel() {
        viewModel = ProductViewModel(
            getProductsUseCase,
            getProductDetailUseCase,
            createProductUseCase,
            updateProductUseCase,
            deleteProductUseCase,
            networkMonitor,
            syncManager
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    // -------------------- OBSERVE NETWORK --------------------
    @Test
    fun `observeNetwork sets offline state`() = runTest {
        every { networkMonitor.observe() } returns MutableStateFlow(false)

        initViewModel()

        assertEquals(true, viewModel.uiState.value.isOffline)
    }

    // -------------------- OBSERVE PRODUCTS --------------------
    @Test
    fun `observeProducts updates products list`() = runTest {
        val products = listOf(Product(1, "Title", "Desc", "user1", true))
        every { getProductsUseCase.invoke() } returns MutableStateFlow(products)

        initViewModel()

        assertEquals(products, viewModel.uiState.value.products)
    }

    // -------------------- REFRESH --------------------
    @Test
    fun `refresh success updates loading false and clears error`() = runTest {
        coEvery { getProductsUseCase.refresh(1, 10) } returns Result.Success(Unit)
        initViewModel()

        viewModel.refresh(1, 10)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(null, errorMessage)
        }
    }

    @Test
    fun `refresh failure sets errorMessage`() = runTest {
        val errorMsg = "Refresh failed"
        coEvery { getProductsUseCase.refresh(1, 10) } returns Result.Error(-1, errorMsg)
        initViewModel()

        viewModel.refresh(1, 10)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(errorMsg, errorMessage)
        }
    }

    // -------------------- CREATE --------------------
    @Test
    fun `create success clears loading and error`() = runTest {
        coEvery { createProductUseCase.invoke("Title", "Desc") } returns Result.Success(Unit)
        initViewModel()

        viewModel.create("Title", "Desc")
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(null, errorMessage)
        }
    }

    @Test
    fun `create failure sets errorMessage`() = runTest {
        val errorMsg = "Create failed"
        coEvery { createProductUseCase.invoke("Title", "Desc") } returns Result.Error(-1, errorMsg)
        initViewModel()

        viewModel.create("Title", "Desc")
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(errorMsg, errorMessage)
        }
    }

    // -------------------- UPDATE --------------------
    @Test
    fun `update success clears loading and error`() = runTest {
        coEvery { updateProductUseCase.invoke(1, "Title", "Desc") } returns Result.Success(Unit)
        initViewModel()

        viewModel.update(1, "Title", "Desc")
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(null, errorMessage)
        }
    }

    @Test
    fun `update failure sets errorMessage`() = runTest {
        val errorMsg = "Update failed"
        coEvery { updateProductUseCase.invoke(1, "Title", "Desc") } returns Result.Error(-1, errorMsg)
        initViewModel()

        viewModel.update(1, "Title", "Desc")
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(errorMsg, errorMessage)
        }
    }

    // -------------------- DELETE --------------------
    @Test
    fun `delete success clears loading and error`() = runTest {
        coEvery { deleteProductUseCase.invoke(1) } returns Result.Success(Unit)
        initViewModel()

        viewModel.delete(1)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(null, errorMessage)
        }
    }

    @Test
    fun `delete failure sets errorMessage`() = runTest {
        val errorMsg = "Delete failed"
        coEvery { deleteProductUseCase.invoke(1) } returns Result.Error(-1, errorMsg)
        initViewModel()

        viewModel.delete(1)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(errorMsg, errorMessage)
        }
    }

    // -------------------- GET PRODUCT BY ID --------------------
    @Test
    fun `getProductById success updates selectedProduct`() = runTest {
        val product = Product(1, "Title", "Desc", "user1", true)
        coEvery { getProductDetailUseCase.invoke(1) } returns Result.Success(product)
        initViewModel()

        viewModel.getProductById(1)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(product, selectedProduct)
            assertEquals(false, isLoading)
            assertEquals(null, errorMessage)
        }
    }

    @Test
    fun `getProductById failure sets errorMessage`() = runTest {
        val errorMsg = "Product not found"
        coEvery { getProductDetailUseCase.invoke(2) } returns Result.Error(-1, errorMsg)
        initViewModel()

        viewModel.getProductById(2)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(null, selectedProduct)
            assertEquals(false, isLoading)
            assertEquals(errorMsg, errorMessage)
        }
    }
}



