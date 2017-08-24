package handson;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

public class ProductQueryService extends AbstractService {

    public ProductQueryService(SphereClient client) {
        super(client);
    }


    public CompletionStage<PagedQueryResult<Category>> findCategory(final Locale locale, final String name) {
        return client.execute(CategoryQuery.of().byName(locale, name));
    }

    public CompletionStage<PagedQueryResult<ProductProjection>> withCategory(final Category category) {
        return client.execute(ProductProjectionQuery.ofStaged().withPredicates(m -> m.categories().isIn(Arrays.asList(category))));
    }

    public CompletionStage<PagedQueryResult<ProductProjection>> findProductsWithCategory(final Locale locale, final String name) {
        return findCategory(locale, name).thenComposeAsync(
                categoryPagedQueryResult -> withCategory(categoryPagedQueryResult.getResults().get(0)));
    }
}
