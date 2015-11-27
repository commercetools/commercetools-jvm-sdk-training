package handson;

import io.sphere.sdk.carts.*;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
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
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionByIdGet;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import org.javamoney.moneta.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;
import static io.sphere.sdk.models.TextInputHint.*;
import static io.sphere.sdk.products.ProductProjectionType.CURRENT;
import static io.sphere.sdk.utils.SetUtils.asSet;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;

public class Main {
    public static final String CUSTOM_TYPE_KEY = "sap-type5";
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
            final Type typeForCustomLineItems = createTypeForExtendedCustomLineItem(client);
            System.err.println("Created typeForCustomLineItems for extending custom line items with product information:");
            System.err.println(typeForCustomLineItems);

            // We create a cart at some moment, e.g. when the customer logs in
            final Cart cart = createCart(client);
            System.err.println("");
            System.err.println("Empty cart created:");
            System.err.println(cart);

            // When clicked on "add to cart" we add a product to the cart, which contains a non-customized price
            final int quantity = 3;
            final ProductProjection product = getSomeProduct(client);
            final Cart cartWithLineItem = addProductToCart(client, cart, product, quantity);
            System.err.println("");
            System.err.println("Cart with regular line items:");
            System.err.println(cartWithLineItem);

            // When clicked on "refresh price" we replace the line items with custom line items, which contain the price from SAP
            final Cart cartWithProductAndPrice = updateCartWithCustomPrices(client, cartWithLineItem);
            System.err.println("");
            System.err.println("Cart with line items with SAP price:");
            System.err.println(cartWithProductAndPrice);
//
//            final Cart cartWithMoreProductsAndPrices = addProductToCartWithSapPrice(client, cartWithProductAndPrice, product, 2);
//
//            final Cart finalCart = addProductToCartWithSapPrice(client, cartWithMoreProductsAndPrices, getSomeOtherProduct(client), 2);

            // Clean up project to avoid conflicts for next iteration
            cleanUpProject(client, typeForCustomLineItems, cartWithProductAndPrice);
        }
    }

    private static Cart updateCartWithCustomPrices(final SphereClient client, final Cart cart) {
        final Map<String, Map<String, Object>> productInfoMap = new HashMap<>(); // productId -> productInfo map

        // Replace line item for custom line item
        final List<UpdateAction<Cart>> replaceLineItemsUpdateActions = new ArrayList<>(); // list of actions to replace line items for the corresponding custom line items
        for (final LineItem lineItem : cart.getLineItems()) {
            final ProductProjection productFromLineItem = getProductFromLineItem(client, lineItem);
            replaceLineItemsUpdateActions.addAll(actionToReplaceLineItemWithCustomLineItem(productFromLineItem, lineItem));
            productInfoMap.put(lineItem.getProductId(), extractInfoFromProduct(productFromLineItem));
        }
        final Cart cartWithCustomLineItems = execute(client, CartUpdateCommand.of(cart, replaceLineItemsUpdateActions));

        // Update custom line item with product info
        final List<UpdateAction<Cart>> updateProductInfoActions = new ArrayList<>(); // list of actions to update custom line items with the corresponding product info
        for (final CustomLineItem customLineItem : cartWithCustomLineItems.getCustomLineItems()) {
            final Map<String, Object> productInfo = productInfoMap.get(customLineItem.getSlug());
            updateProductInfoActions.add(actionToAddProductInfoFieldsInCustomLineItem(customLineItem, productInfo));
        }
        return execute(client, CartUpdateCommand.of(cartWithCustomLineItems, updateProductInfoActions));
    }

    private static List<UpdateAction<Cart>> actionToReplaceLineItemWithCustomLineItem(final ProductProjection productFromLineItem, final LineItem lineItem) {
        final CustomLineItemDraft customLineItemDraft = createCustomLineItemFromProduct(productFromLineItem, lineItem.getQuantity());
        return asList(AddCustomLineItem.of(customLineItemDraft), RemoveLineItem.of(lineItem));
    }

    private static UpdateAction<Cart> actionToAddProductInfoFieldsInCustomLineItem(final CustomLineItem customLineItem,
                                                                                   final Map<String, Object> productInfo) {
        return SetCustomLineItemCustomType.ofTypeKeyAndObjects(CUSTOM_TYPE_KEY, productInfo, customLineItem.getId());
    }

    private static Map<String, Object> extractInfoFromProduct(final ProductProjection product) {
        final Map<String, Object> productInfo = new HashMap<>();
        productInfo.put(PRODUCT_ID_FIELD, product.getId());
        productInfo.put(PRODUCT_SLUG_FIELD, product.getSlug());
        productInfo.put(PRODUCT_SKU_FIELD, product.getMasterVariant().getSku());
        return productInfo;
    }

    private static CustomLineItemDraft createCustomLineItemFromProduct(final ProductProjection product, final long quantity) {
        return CustomLineItemDraft.of(product.getName(), product.getId(), getFinalPriceFromSap(), product.getTaxCategory(), quantity);
    }

    private static Cart addProductToCart(final SphereClient client, final Cart cart, final ProductProjection product, final int quantity) {
        return execute(client, CartUpdateCommand.of(cart, AddLineItem.of(product, product.getMasterVariant().getId(), quantity)));
    }

//    private static Cart addProductToCartWithSapPrice(final SphereClient client, final Cart cart, final ProductProjection product, final int quantity) {
//        final Optional<CustomLineItem> customLineItemForProduct = findCustomLineItemForProduct(cart, product);
//        if (customLineItemForProduct.isPresent()) {
//            customLineItemForProduct.
//        } else {
//
//        }
//
//        final CustomLineItemDraft customLineItemDraft = createCustomLineItemFromProduct(product, quantity);
//        final Cart cartWithCustomLineItem = execute(client, CartUpdateCommand.of(cart, AddCustomLineItem.of(customLineItemDraft)));
//
//        final Map<String, Object> productInfo = extractInfoFromProduct(product);
//        final List<UpdateAction<Cart>> updateProductInfoActions = new ArrayList<>();
//        for (final CustomLineItem customLineItem : findCustomLineItemsWithoutProductInfo(cartWithCustomLineItem, product)) {
//             updateProductInfoActions.add(actionToAddProductInfoFieldsInCustomLineItem(customLineItem, productInfo));
//        }
//        return execute(client, CartUpdateCommand.of(cartWithCustomLineItem, updateProductInfoActions));
//    }

    private static Optional<CustomLineItem> findCustomLineItemForProduct(final Cart cart, final ProductProjection product) {
        return cart.getCustomLineItems().stream()
                .filter(cli -> cli.getSlug().equals(product.getId()))
                .findFirst();
    }

    private static Type createTypeForExtendedCustomLineItem(final SphereClient client) {
        // This is only executed (or created) once, this doesn't need to be here in a real example
        final TypeDraft typeDraft = TypeDraftBuilder.of(CUSTOM_TYPE_KEY, LocalizedString.of(ENGLISH, "CustomLineItem with product"), asSet("custom-line-item"))
                .fieldDefinitions(asList(
                        FieldDefinition.of(StringType.of(), PRODUCT_ID_FIELD, LocalizedString.of(ENGLISH, "Product ID"), true, SINGLE_LINE),
                        FieldDefinition.of(LocalizedStringType.of(), PRODUCT_SLUG_FIELD, LocalizedString.of(ENGLISH, "Product Slug"), true, SINGLE_LINE),
                        FieldDefinition.of(StringType.of(), PRODUCT_SKU_FIELD, LocalizedString.of(ENGLISH, "SKU"), true, SINGLE_LINE)))
                .build();
        return execute(client, TypeCreateCommand.of(typeDraft));
    }

    private static ProductProjection getProductFromLineItem(final SphereClient client, final LineItem lineItem) {
        return execute(client, ProductProjectionByIdGet.of(lineItem.getProductId(), CURRENT));
    }

    private static Cart createCart(final SphereClient client) {
        return execute(client, CartCreateCommand.of(CartDraft.of(EUR)));
    }

    private static Money getFinalPriceFromSap() {
        return Money.of(BigDecimal.TEN, EUR); // TODO fetch price from SAP
    }

    private static ProductProjection getSomeProduct(final SphereClient client) {
        return execute(client, ProductProjectionQuery.ofCurrent()).getResults().get(0);
    }

    private static ProductProjection getSomeOtherProduct(final SphereClient client) {
        return execute(client, ProductProjectionQuery.ofCurrent()).getResults().get(1);
    }

    private static Properties loadCommercetoolsPlatformProperties() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        return prop;
    }

    private static void cleanUpProject(final SphereClient client, final Type typeForCustomLineItems, final Cart cartWithProductAndPrice) {
        execute(client, CartDeleteCommand.of(cartWithProductAndPrice));
        execute(client, TypeDeleteCommand.of(typeForCustomLineItems));
    }

    private static <T> T execute(final SphereClient client, final SphereRequest<T> sphereRequest) {
        return client.execute(sphereRequest).toCompletableFuture().join();
    }
}
