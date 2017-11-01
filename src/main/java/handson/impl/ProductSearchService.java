package handson.impl;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.attributes.StringAttributeType;
import io.sphere.sdk.search.PagedSearchResult;

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
        return null;
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
        return null;
    }
}
