package handson;

import handson.impl.ProductSearchService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.search.TermFacetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Full text search and term facets search for products.
 *
 * See:
 *  TODO Task08.1 {@link ProductSearchService#fulltextSearch(LocalizedStringEntry)}
 *  TODO Task08.2 {@link ProductSearchService#facetSearch(String, String)}
 */
public class Moodle14_SEARCH {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle14_SEARCH.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final ProductSearchService productSearchService = new ProductSearchService(client);


            // TODO: Search products using the ProductProjections API
            //
            PagedSearchResult<ProductProjection> foundProducts =
                    productSearchService.fulltextSearch(LocalizedStringEntry.of("en", "Red-wine"))
                            .toCompletableFuture().get();
            LOG.info("Found products: {}", foundProducts.getTotal());


            // TODO: Search products and facets using the ProductProjectionsSearch API
            //
            foundProducts =
                    productSearchService.facetSearch("deepness", "super deep")
                                        .toCompletableFuture().get();
            LOG.info("Returned facets: {}", foundProducts.getFacetsResults());
            LOG.info("Found products: {}", foundProducts.getTotal());


        }
    }



    // Additional material for inspection after class
    //
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
