package handson;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductQueryServiceTest extends BaseTest {
    private ProductQueryService productQueryService;

    @Before
    public void setup() throws IOException {
        super.setup();
        productQueryService = new ProductQueryService(client());
    }

    @Test
    public void findCategory() throws ExecutionException, InterruptedException {
        final CompletableFuture<PagedQueryResult<Category>> findCategoryResult =
                productQueryService.findCategory(Locale.ENGLISH, "Brands").toCompletableFuture();
        final PagedQueryResult<Category> categoryPagedQueryResult = findCategoryResult.get();

        assertThat(categoryPagedQueryResult.getResults()).hasSize(1);
    }

    @Test
    public void withCategory() throws ExecutionException, InterruptedException {
        final CompletableFuture<PagedQueryResult<Category>> findCategoryResult =
                productQueryService.findCategory(Locale.ENGLISH, "Sale").toCompletableFuture();
        final PagedQueryResult<Category> categoryPagedQueryResult = findCategoryResult.get();

        assertThat(categoryPagedQueryResult.getTotal()).isEqualTo(1);
        final Category category = categoryPagedQueryResult.getResults().get(0);

        final CompletableFuture<PagedQueryResult<ProductProjection>> productsWithCategoryResult =
                productQueryService.withCategory(category).toCompletableFuture();

        final PagedQueryResult<ProductProjection> productsWithCategory = productsWithCategoryResult.get();
        assertThat(productsWithCategory.getResults()).isNotEmpty();
    }

}
