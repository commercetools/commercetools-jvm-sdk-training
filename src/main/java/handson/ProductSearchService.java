package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.search.PagedSearchResult;

import java.util.concurrent.CompletionStage;

public class ProductSearchService extends AbstractService {

    public ProductSearchService(SphereClient client) {
        super(client);
    }

    public CompletionStage<PagedSearchResult<ProductProjection>> fulltextSearch(final LocalizedStringEntry searchText) {
        return client.execute(ProductProjectionSearch.ofCurrent().withText(searchText));
    }

    public CompletionStage<PagedSearchResult<ProductProjection>> facetSearch(final String attributeName, String attributeValue) {
        return client.execute(ProductProjectionSearch.ofStaged()
                .plusQueryFilters(productModel -> productModel.allVariants().attribute().ofString(attributeName).is(attributeValue)));
    }
}
