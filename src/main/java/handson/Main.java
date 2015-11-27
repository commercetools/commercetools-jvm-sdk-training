package handson;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.CustomLineItemDraft;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddCustomLineItem;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetCustomLineItemCustomType;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.javamoney.moneta.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static io.sphere.sdk.models.TextInputHint.*;
import static io.sphere.sdk.utils.SetUtils.asSet;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;

public class Main {

    public static final String SKU = "book-sku"; // TODO set a real sku in your project which has a tax category!
    public static final String CUSTOM_TYPE_KEY = "sap-type";
    public static final String PRODUCT_ID_FIELD = "productId";
    public static final String PRODUCT_SLUG_FIELD = "productSlug";
    public static final String PRODUCT_SKU_FIELD = "productSku";

    public static void main(String[] args) throws IOException {
        final Properties prop = loadCommercetoolsPlatformProperties();
        final String projectKey = prop.getProperty("projectKey");
        final String clientId = prop.getProperty("clientId");
        final String clientSecret = prop.getProperty("clientSecret");
        final SphereClientConfig clientConfig = SphereClientConfig.of(projectKey, clientId, clientSecret);

        try(final SphereClient client = SphereClientFactory.of().createClient(clientConfig)) {
            // Done only once at creation of the project
            createTypeForExtendedCustomLineItem(client);

            final Cart cart = createCart(client);
            final ProductProjection product = getSomeProduct(client);

            final int quantity = 3;
            final Cart cartWithProductAndPrice = addLineItemWithPriceFromSap(client, product, cart, quantity);
            System.err.println(cartWithProductAndPrice);
        }
    }

    private static Cart addLineItemWithPriceFromSap(final SphereClient client, final ProductProjection product,
                                                    final Cart cart, final int quantity) {
        final Cart cartWithLineItem = addProductToCart(client, cart, product, quantity);
        final Cart cartWithCustomLineItem = replaceLineItemWithCustomLineItem(client, product, cartWithLineItem);
        return addProductInfoFieldsInCustomLineItem(client, product, cartWithCustomLineItem);
    }

    private static Cart addProductInfoFieldsInCustomLineItem(final SphereClient client, final ProductProjection product, final Cart cartWithCustomLineItem) {
        final Map<String, Object> values = new HashMap<>();
        values.put(PRODUCT_ID_FIELD, product.getId());
        values.put(PRODUCT_SLUG_FIELD, product.getSlug());
        values.put(PRODUCT_SKU_FIELD, product.getMasterVariant().getSku());
        final SetCustomLineItemCustomType setCustomLineItemCustomType = SetCustomLineItemCustomType
                .ofTypeKeyAndObjects(CUSTOM_TYPE_KEY, values, getCustomLineItemInCart(cartWithCustomLineItem));
        return execute(client, CartUpdateCommand.of(cartWithCustomLineItem, setCustomLineItemCustomType));
    }

    private static Cart replaceLineItemWithCustomLineItem(final SphereClient client, final ProductProjection product, final Cart cart) {
        final LineItem lineItemInCart = getLineItemInCart(cart);
        final CustomLineItemDraft customLineItemDraft = CustomLineItemDraft.of(product.getName(), "product-sap", getFinalPriceFromSap(), product.getTaxCategory(), lineItemInCart.getQuantity());
        final List<UpdateAction<Cart>> updateActions = asList(AddCustomLineItem.of(customLineItemDraft), RemoveLineItem.of(lineItemInCart));
        return execute(client, CartUpdateCommand.of(cart, updateActions));
    }

    private static Cart addProductToCart(final SphereClient client, final Cart cart, final ProductProjection product, final int quantity) {
        return execute(client, CartUpdateCommand.of(cart, AddLineItem.of(product, product.getMasterVariant().getId(), quantity)));
    }

    private static Cart createCart(final SphereClient client) {
        return execute(client, CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR)));
    }

    private static Type createTypeForExtendedCustomLineItem(final SphereClient client) {
        // This is only executed (or created) once, this doesn't need to be here in a real example
        final PagedQueryResult<Type> result = execute(client, TypeQuery.of().withPredicates(type -> type.key().is(CUSTOM_TYPE_KEY)));

        if (result.getResults().isEmpty()) {
            final TypeDraft typeDraft = TypeDraftBuilder.of(CUSTOM_TYPE_KEY, LocalizedString.of(ENGLISH, "CustomLineItem with product"), asSet("custom-line-item"))
                    .fieldDefinitions(asList(
                            FieldDefinition.of(StringType.of(), PRODUCT_ID_FIELD, LocalizedString.of(ENGLISH, "Product ID"), true, SINGLE_LINE),
                            FieldDefinition.of(LocalizedStringType.of(), PRODUCT_SLUG_FIELD, LocalizedString.of(ENGLISH, "Product Slug"), true, SINGLE_LINE),
                            FieldDefinition.of(StringType.of(), PRODUCT_SKU_FIELD, LocalizedString.of(ENGLISH, "SKU"), true, SINGLE_LINE)))
                    .build();
            return execute(client, TypeCreateCommand.of(typeDraft));
        } else {
            return result.getResults().get(0);
        }
    }

    private static String getCustomLineItemInCart(final Cart cartWithCustomLineItem) {
        return cartWithCustomLineItem.getCustomLineItems().get(0).getId(); // TODO find nicely by slug
    }

    private static LineItem getLineItemInCart(final Cart cart) {
        return cart.getLineItems().get(0);
    }

    private static Money getFinalPriceFromSap() {
        return Money.of(BigDecimal.TEN, DefaultCurrencyUnits.EUR); // TODO fetch price from SAP
    }

    private static ProductProjection getSomeProduct(final SphereClient client) {
        final PagedQueryResult<ProductProjection> result = execute(client, ProductProjectionQuery.ofCurrent()
                .withPredicates(product -> product.allVariants().where(variant -> variant.sku().is(SKU))));
        return result.getResults().get(0);
    }

    private static Properties loadCommercetoolsPlatformProperties() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        return prop;
    }

    private static <T> T execute(final SphereClient client, final SphereRequest<T> sphereRequest) {
        return client.execute(sphereRequest).toCompletableFuture().join();
    }
}
