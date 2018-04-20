package handson;

import handson.impl.ProductSearchService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.search.PagedSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Full text search and term facets search for products.
 */
public class Exercise8 {
    private final static Logger LOG = LoggerFactory.getLogger(Exercise8.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final ProductSearchService productSearchService = new ProductSearchService(client);

            final CompletableFuture<PagedSearchResult<ProductProjection>> fulltextSearchResult =
                    productSearchService.fulltextSearch(LocalizedStringEntry.of("en", "Cantarelli"))
                            .toCompletableFuture();
            PagedSearchResult<ProductProjection> foundProducts = fulltextSearchResult.get();

            LOG.info("Found products: {}", foundProducts.getTotal());

            final CompletableFuture<PagedSearchResult<ProductProjection>> facetSearchResult =
                    productSearchService.facetSearch("color", "red")
                            .toCompletableFuture();
            foundProducts = facetSearchResult.get();
            LOG.info("Returned facets:", foundProducts.getFacetsResults());
            LOG.info("Found products: {}", foundProducts.getTotal());
        }
    }
}
