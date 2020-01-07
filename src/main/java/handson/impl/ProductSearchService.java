package handson.impl;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.Identifiable;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.attributes.StringAttributeType;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.PagedResult;
import io.sphere.sdk.search.PagedSearchResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This class provides search operations for {@link ProductProjection}s.
 */
public class ProductSearchService extends AbstractService {

    public ProductSearchService(SphereClient client) {
        super(client);
    }

    /**
     * Performs a full-text search for the given search text.
     *
     * @param searchText the search text
     * @return the full-text search completion stage
     */
    public CompletionStage<PagedSearchResult<ProductProjection>> fulltextSearch(final LocalizedStringEntry searchText) {
        // TODO 8.1 Perform a full-text search

        return
                null;
    }

    /**
     * Performs a term facet search for the given attribute value.
     *
     * @param attributeName  the attribute name (we assume that the attribute is a {@link StringAttributeType})
     * @param attributeValue the attribute value
     * @return the term facet search completion stage
     */
    public CompletionStage<PagedSearchResult<ProductProjection>> facetSearch(final String attributeName, String attributeValue) {
        // TODO 8.2 Perform a term facet search

        return
                null;
    }

    public CompletionStage<List<Product>> findNext(final ProductQuery seedQuery, final ProductQuery query, final List<Product> products, final int PAGE_SIZE) {
        return
                null;
    }


    private <T extends Identifiable<T>> String getIdForNextQuery(final PagedResult<T> pagedResult) {
        final List<T> results = pagedResult.getResults();
        final int indexLastElement = results.size() - 1;
        return results.get(indexLastElement).getId();
    }




}
