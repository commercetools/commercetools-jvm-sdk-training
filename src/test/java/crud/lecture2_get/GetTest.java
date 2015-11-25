package crud.lecture2_get;

import intern.BaseTest;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariantDraft;
import io.sphere.sdk.products.ProductVariantDraftBuilder;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeByKeyGet;
import io.sphere.sdk.queries.PagedQueryResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class GetTest extends BaseTest {

    public static final String PRODUCT_TYPE_KEY = "tshirt";

    @Test
    public void queryProductBySlug() {
        final PagedQueryResult<ProductProjection> queryResult = ct().complete(ProductProjectionQuery.ofStaged().withPredicates(p -> TODO()));
        assertThat(queryResult.getResults()).hasSize(3).extracting("slug")
                .contains(LocalizedString.of(Locale.ENGLISH, "foo-tshirt-1"),
                        LocalizedString.of(Locale.ENGLISH, "foo-tshirt-2"),
                        LocalizedString.of(Locale.ENGLISH, "foo-tshirt-3"));
    }

    @BeforeClass
    @AfterClass
    public void cleanup() {
        final ProductType productType = ct().complete(ProductTypeByKeyGet.of(PRODUCT_TYPE_KEY));
        if (productType != null) {
            ct().complete(ProductQuery.of().byProductType(productType)).getResults()
                    .forEach(p -> ct().complete(ProductDeleteCommand.of(p)));
            ct().complete(ProductTypeDeleteCommand.of(productType));
        }
    }

    @BeforeClass
    public void setup() {
        final ProductType productType = ct().complete(ProductTypeCreateCommand.of(ProductTypeDraft.of(PRODUCT_TYPE_KEY, "tshirt", "a t shaped cloth", Collections.emptyList())));
        final LocalizedString name = LocalizedString.of(Locale.ENGLISH, "foo");
        final ProductVariantDraft productVariantDraft = ProductVariantDraftBuilder.of().build();
        for (int i = 0; i < 20; i++) {
            ct().complete(ProductCreateCommand.of(ProductDraftBuilder.of(productType, name, LocalizedString.of(Locale.ENGLISH, "foo-tshirt-" + i), productVariantDraft).build()));
        }
    }


}
