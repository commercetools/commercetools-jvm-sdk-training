package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.search.TermFacetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;

public class FiltersAndFacetsExample {
    private final static Logger LOG = LoggerFactory.getLogger(FiltersAndFacetsExample.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            createFacets(client);

            filterProductResults(client);

            filterFacets(client);
        }
    }

    private static void createFacets(final SphereClient client) throws InterruptedException, ExecutionException {
        LOG.info("Exercise 1: Create Facets\n");

        PagedSearchResult<ProductProjection> searchResult = client.execute(ProductProjectionSearch.ofCurrent()
                .plusFacets(m -> m.allVariants().attribute().ofString("color").allTerms())
                .plusFacets(m -> m.allVariants().attribute().ofString("material").allTerms())
                .withLimit(0L))
                .toCompletableFuture().get();
        LOG.info("Facets:");
        TermFacetResult colorFacetResult = (TermFacetResult) searchResult.getFacetResult("variants.attributes.color");
        LOG.info("  # of colors: {}", colorFacetResult.getTotal());
        LOG.info("  Terms of colors: {}", colorFacetResult.getTerms());

        TermFacetResult materialFacetResult = (TermFacetResult) searchResult.getFacetResult("variants.attributes.material");
        LOG.info("  # of material: {}", materialFacetResult.getTotal());
        LOG.info("  Terms of material: {}", materialFacetResult.getTerms());
    }

    private static void filterProductResults(final SphereClient client) throws InterruptedException, ExecutionException {
        LOG.info("Exercise 2: Filter Product Results\n");

        PagedSearchResult<ProductProjection> searchResult = client.execute(ProductProjectionSearch.ofCurrent()
                .withQueryFilters(m -> m.allVariants().attribute().ofString("color").is("red"))
                .plusFacets(m -> m.allVariants().attribute().ofString("color").allTerms())
                .plusFacets(m -> m.allVariants().attribute().ofString("material").allTerms())
                .withMarkingMatchingVariants(true))
                .toCompletableFuture().get();

        LOG.info("Found results: {}", searchResult.getTotal());
        LOG.info("Facets:");
        TermFacetResult colorFacetResult = (TermFacetResult) searchResult.getFacetResult("variants.attributes.color");
        LOG.info("  # of colors: {}", colorFacetResult.getTotal());
        LOG.info("  Terms of colors: {}", colorFacetResult.getTerms());

        TermFacetResult materialFacetResult = (TermFacetResult) searchResult.getFacetResult("variants.attributes.material");
        LOG.info("  # of material: {}", materialFacetResult.getTotal());
        LOG.info("  Terms of material: {}", materialFacetResult.getTerms());

        LOG.info("Results: {}", searchResult.getResults());
    }

    private static void filterFacets(final SphereClient client) throws InterruptedException, ExecutionException {
        LOG.info("Exercise 3: Filter Facets\n");

        PagedSearchResult<ProductProjection> searchResult = client.execute(ProductProjectionSearch.ofCurrent()
                .withQueryFilters(m -> m.allVariants().attribute().ofString("color").is("red"))
                .plusFacets(m -> m.allVariants().attribute().ofString("material").allTerms())
                .plusFacetFilters(m -> m.allVariants().attribute().ofString("color").is("red")))
                .toCompletableFuture().get();

        LOG.info("Found results: {}", searchResult.getTotal());
        LOG.info("Facets:");

        TermFacetResult materialFacetResult = (TermFacetResult) searchResult.getFacetResult("variants.attributes.material");
        LOG.info("  # of material: {}", materialFacetResult.getTotal());
        LOG.info("  Terms of material: {}", materialFacetResult.getTerms());
    }
}