package handson.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.LocalizedStringEntry;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariantDraft;
import io.sphere.sdk.products.ProductVariantDraftBuilder;
import io.sphere.sdk.products.attributes.AttributeDraft;
import io.sphere.sdk.products.attributes.AttributeDraftBuilder;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.search.PagedSearchResult;

public class ProductSearchServiceTest extends BaseTest {
    private ProductSearchService productSearchService;
    private ProductSearchService facetProductSearchService;

    @Before
    public void setup() throws IOException {
        super.setup();
        
        try {
			
        	PagedQueryResult<ProductType> productTypeResults = client()
				.execute(ProductTypeQuery.of())
				.toCompletableFuture()
				.get();
			ProductType productType = productTypeResults.getResults().get(0);
			
			ProductVariantDraft variant = ProductVariantDraftBuilder.of()
				.sku("Cantarelli")
				.attributes(AttributeDraftBuilder.of(AttributeDraft.of("color", "red")).build())
				.build();
			
			ProductDraftBuilder.of(productType, 
				LocalizedString.ofEnglish("Cantarelli"), 
				LocalizedString.ofEnglish("Cantarelli"), 
				Arrays.asList(variant));
			
			
			
		} catch (InterruptedException | ExecutionException e) {
			/* Do nothing */
		}
        
        productSearchService = new ProductSearchService(client());
        facetProductSearchService = new ProductSearchService(client());
    }

    @Test
    public void fulltextSearch() throws ExecutionException, InterruptedException {
    	final CompletableFuture<PagedSearchResult<ProductProjection>> fulltextSearchResult =
                productSearchService.fulltextSearch(LocalizedStringEntry.of("en", "Cantarelli"))
                    .toCompletableFuture();
        final PagedSearchResult<ProductProjection> foundProducts = fulltextSearchResult.get();
        assertThat(foundProducts.getResults()).isNotEmpty();
    }

    @Test
    public void facetSearch() throws Exception {
        final CompletableFuture<PagedSearchResult<ProductProjection>> facetSearchResult =
                facetProductSearchService.facetSearch("color", "red")
                    .toCompletableFuture();
        final PagedSearchResult<ProductProjection> foundProducts = facetSearchResult.get();
        assertThat(foundProducts.getResults()).isNotEmpty();
    }
}
