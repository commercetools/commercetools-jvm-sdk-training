package handson.impl;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.search.PagedSearchResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSearchServiceTest extends BaseTest {
    private ProductSearchService productSearchService;
    private ProductSearchService facetProductSearchService;

    @Before
    public void setup() throws IOException {
        super.setup();
        productSearchService = new ProductSearchService(client());
        final SphereClient sphereClient = createSphereClient("/facets-dev.properties");
        facetProductSearchService = new ProductSearchService(sphereClient);
    }

    @Test
    public void fulltextSearch() throws ExecutionException, InterruptedException {
        final CompletableFuture<PagedSearchResult<ProductProjection>> fulltextSearchResult =
                productSearchService.fulltextSearch(LocalizedStringEntry.of("en", "Cantarelli"))
                    .toCompletableFuture();
        final PagedSearchResult<ProductProjection> foundProducts = fulltextSearchResult.get();
        assertThat(foundProducts.getResults()).isNotEmpty();
    }

    @Test
    public void facetSearch() throws Exception {
        final CompletableFuture<PagedSearchResult<ProductProjection>> facetSearchResult =
                facetProductSearchService.facetSearch("color", "red")
                    .toCompletableFuture();
        final PagedSearchResult<ProductProjection> foundProducts = facetSearchResult.get();
        assertThat(foundProducts.getResults()).isNotEmpty();
    }
}
