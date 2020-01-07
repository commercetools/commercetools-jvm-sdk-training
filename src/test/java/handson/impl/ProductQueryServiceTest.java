package handson.impl;

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
    public void findProductsWithCategory() throws ExecutionException, InterruptedException {
        final CompletableFuture<PagedQueryResult<ProductProjection>> productsOnSaleResult =
                productQueryService.findProductsWithCategory(Locale.ENGLISH, "Sale").toCompletableFuture();
        final PagedQueryResult<ProductProjection> productsOnSale = productsOnSaleResult.get();
        assertThat(productsOnSale.getResults()).isNotEmpty();
    }
}
