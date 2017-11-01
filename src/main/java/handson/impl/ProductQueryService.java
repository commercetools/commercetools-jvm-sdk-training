package handson.impl;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Locale;
import java.util.concurrent.CompletionStage;

/**
 * This class provides query operations for {@link ProductProjection}s.
 */
public class ProductQueryService extends AbstractService {

    public ProductQueryService(SphereClient client) {
        super(client);
    }

    /**
     * Finds products with categories that have the given localized name.
     *
     * @param locale the locale
     * @param name   the localized name
     * @return the product query completion stage
     */
    public CompletionStage<PagedQueryResult<ProductProjection>> findProductsWithCategory(final Locale locale, final String name) {
        // TODO 4.3 Find a product with category
        return null;
    }

    private CompletionStage<PagedQueryResult<Category>> findCategory(final Locale locale, final String name) {
        // TODO 4.1 Find a category
        return null;
    }

    private CompletionStage<PagedQueryResult<ProductProjection>> withCategory(final Category category) {
        // TODO 4.2 Query a category
        return null;
    }
}
