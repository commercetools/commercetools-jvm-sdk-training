package handson.impl;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Arrays;
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
        return findCategory(locale, name).thenComposeAsync(
                categoryPagedQueryResult -> withCategory(categoryPagedQueryResult.getResults().get(0)));
    }

    private CompletionStage<PagedQueryResult<Category>> findCategory(final Locale locale, final String name) {
        return client.execute(CategoryQuery.of().byName(locale, name));
    }

    private CompletionStage<PagedQueryResult<ProductProjection>> withCategory(final Category category) {
        return client.execute(ProductProjectionQuery.ofStaged().withPredicates(m -> m.categories().isIn(Arrays.asList(category))));
    }
}
