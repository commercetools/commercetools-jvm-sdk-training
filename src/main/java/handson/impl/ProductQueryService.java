package handson.impl;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.PagedResult;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.Collections.singletonList;

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
        return findCategory(locale, name)
            .thenApplyAsync(PagedResult::head)
            .thenComposeAsync(categoryOptional ->
                categoryOptional.map(this::withCategory)
                                .orElseGet(() -> CompletableFuture.completedFuture(PagedQueryResult.empty())));
    }

    private CompletionStage<PagedQueryResult<Category>> findCategory(final Locale locale, final String name) {
        return client.execute(CategoryQuery.of().byName(locale, name));
    }

    private CompletionStage<PagedQueryResult<ProductProjection>> withCategory(final Category category) {
        final ProductProjectionQuery query = ProductProjectionQuery.ofStaged()
                                                                   .withPredicates(m -> m.categories().isIn(singletonList(category)));

        return client.execute(query);
    }
}
