package handson;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.commands.OrderDeleteCommand;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.attributes.AttributeDefinitionBuilder;
import io.sphere.sdk.products.attributes.BooleanAttributeType;
import io.sphere.sdk.products.attributes.StringAttributeType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;
import static io.sphere.sdk.models.TextInputHint.SINGLE_LINE;
import static io.sphere.sdk.utils.SphereInternalUtils.asSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;

/**
 * All these methods are typically created once per project, so none of this should be here in a real example
 */
public class ProjectInitialization {

    public static final String PRODUCT_TYPE_KEY = RandomStringUtils.randomAlphanumeric(10);
    public static final String ORDER_WITH_FRAUD_SCORE_TYPE_KEY = RandomStringUtils.randomAlphanumeric(10);
    public static final String FRAUD_SCORE_FIELD = "fraudScore";

    public static ProductType productType;
    public static Product shirt;
    public static Product pants;
    public static Type typeForCarts;

    /**
     * Set up project with the necessary data.
     * @param client CTP client
     */
    public static void setUpProject(final BlockingSphereClient client) {
        System.err.println("** START SETTING PROJECT UP **");
        // Fetch first tax category we find, for simplicity
        final TaxCategory taxCategory = fetchFirstFoundTaxCategory(client);

        // Create two example products (done only once at the creation of the project, usually on import process)
        productType = createProductType(client);
        System.err.println("Created product type");

        shirt = createProduct(client, productType, taxCategory, "shirt");
        System.err.println("Created product 'shirt' with price " + findFormattedPrice(shirt).orElse("???"));
        pants = createProduct(client, productType, taxCategory, "pants");
        System.err.println("Created product 'pants' with price " + findFormattedPrice(pants).orElse("???"));

        // Create Field Type for Cart and Order with Fraud Score (done only once at creation of the project, usually on import process)
        typeForCarts = createFraudScoreTypeForOrders(client);
        System.err.println("Created typeForCart for extending carts with fraud score");
        System.err.println(typeForCarts);
        System.err.println("** FINISH SETTING PROJECT UP **");
    }

    /**
     * Clean up the created data for the project. To avoid conflicts during the next execution.
     * @param client CTP client
     * @param order the order created during the execution
     */
    public static void cleanUpProject(final BlockingSphereClient client, final Order order) {
        System.err.println("");
        System.err.println("** START CLEANING PROJECT UP **");
        // Delete the order and its cart

        deleteOrder(client, order);
        System.err.println("Deleted order and cart");

        // Delete custom type for Cart and Order
        deleteFraudScoreTypeForOrders(client, typeForCarts);
        System.err.println("Deleted typeForCart for extending carts with fraud score");

        // Delete products
        deleteProduct(client, pants);
        System.err.println("Deleted product 'pants'");
        deleteProduct(client, shirt);
        System.err.println("Deleted product 'shirt'");

        // Delete the product type
        deleteProductType(client, productType);
        System.err.println("Deleted product type");
        System.err.println("** FINISH CLEANING PROJECT UP **");
    }

    /**
     * Fetches the first Tax Category found in the project, otherwise it throws an exception.
     * @param client CTP client
     * @return the first found Tax Category
     * @throws RuntimeException if there is no Tax Category defined in the project
     */
    private static TaxCategory fetchFirstFoundTaxCategory(final BlockingSphereClient client) {
        // Fetch first tax category we find, for simplicity
        final TaxCategoryQuery query = TaxCategoryQuery.of();
        return client.executeBlocking(query).head()
                .orElseThrow(() -> new RuntimeException("Missing a tax category, you need to manually create one for your project"));
    }

    /**
     * Creates a Type for Cart and Order with one attribute:
     * - fraudScore, of type Number
     * @param client CTP client
     * @return the created type
     */
    private static Type createFraudScoreTypeForOrders(final BlockingSphereClient client) {
        final LocalizedString cartTypeName = LocalizedString.of(ENGLISH, "Cart with fraud score");
        final TypeDraft typeDraft = TypeDraftBuilder.of(ORDER_WITH_FRAUD_SCORE_TYPE_KEY, cartTypeName, asSet("order"))
                .fieldDefinitions(singletonList(
                        FieldDefinition.of(NumberFieldType.of(), FRAUD_SCORE_FIELD, LocalizedString.of(ENGLISH, "Fraud score"), false, SINGLE_LINE)
                )).build();
        final TypeCreateCommand createCommand = TypeCreateCommand.of(typeDraft);
        return client.executeBlocking(createCommand);
    }

    /**
     * Creates a Product Type with two attributes:
     * - "color", of type String
     * - "handmade", of type Boolean
     * @param client CTP client
     * @return the created product type
     */
    private static ProductType createProductType(final BlockingSphereClient client) {
        final ProductTypeDraft draft = ProductTypeDraft.of(PRODUCT_TYPE_KEY, PRODUCT_TYPE_KEY, "", asList(
                AttributeDefinitionBuilder.of("color", LocalizedString.of(ENGLISH, "Color"), StringAttributeType.of()).build(),
                AttributeDefinitionBuilder.of("handmade", LocalizedString.of(ENGLISH, "Handmade"), BooleanAttributeType.of()).build()));
        final ProductTypeCreateCommand createCommand = ProductTypeCreateCommand.of(draft);
        return client.executeBlocking(createCommand);
    }

    /**
     * Creates a Product with the given name and random slug and price in EUR.
     * @param client CTP client
     * @param productType the Product Type defining the allowed attributes of the product
     * @param taxCategory the Tax Category assigned to the product
     * @param name the name of the product
     * @return the created product
     */
    private static Product createProduct(final BlockingSphereClient client, final ProductType productType,
                                         final TaxCategory taxCategory, final String name) {
        final LocalizedString localizedName = LocalizedString.of(ENGLISH, name);
        final LocalizedString randomSlug = LocalizedString.of(ENGLISH, RandomStringUtils.randomAlphanumeric(10));
        final ProductVariantDraft masterVariant = ProductVariantDraftBuilder.of()
                .price(PriceDraft.of(BigDecimal.valueOf(RandomUtils.nextLong(1, 50)), EUR))
                .build();
        final ProductDraft draft = ProductDraftBuilder.of(productType, localizedName, randomSlug, masterVariant)
                .taxCategory(taxCategory)
                .build();
        final ProductCreateCommand createCommand = ProductCreateCommand.of(draft);
        return client.executeBlocking(createCommand);
    }

    /**
     * Deletes the given Product.
     * @param client CTP client
     * @param product Product to delete
     */
    private static void deleteProduct(final BlockingSphereClient client, final Product product) {
        final ProductDeleteCommand deleteCommand = ProductDeleteCommand.of(product);
        client.executeBlocking(deleteCommand);
    }

    /**
     * Deletes the Order and the associated Cart.
     * @param client CTP client
     * @param order the order to be deleted
     */
    private static void deleteOrder(final BlockingSphereClient client, final Order order) {
        final OrderDeleteCommand orderDeleteCommand = OrderDeleteCommand.of(order);
        client.executeBlocking(orderDeleteCommand);
        if (order.getCart() != null && order.getCart().getId() != null) {
            final Cart cart = client.executeBlocking(CartByIdGet.of(order.getCart().getId()));
            final CartDeleteCommand cartDeleteCommand = CartDeleteCommand.of(cart);
            client.executeBlocking(cartDeleteCommand);
        }
    }

    /**
     * Deletes the Type for Fraud Score.
     * @param client CTP client
     * @param typeForCarts the type to be deleted
     */
    private static void deleteFraudScoreTypeForOrders(final BlockingSphereClient client, final Type typeForCarts) {
        final TypeDeleteCommand deleteCommand = TypeDeleteCommand.of(typeForCarts);
        client.executeBlocking(deleteCommand);
    }

    /**
     * Deletes the Product Type.
     * @param client CTP client
     * @param productType the product type to be deleted
     */
    private static void deleteProductType(final BlockingSphereClient client, final ProductType productType) {
        final ProductTypeDeleteCommand deleteCommand = ProductTypeDeleteCommand.of(productType);
        client.executeBlocking(deleteCommand);
    }

    /**
     * Finds first valid price for the given product.
     * @param product Product which price is requested
     * @return the formatted price of the product, if any
     */
    private static Optional<String> findFormattedPrice(final Product product) {
        return product.getMasterData().getStaged().getMasterVariant().getPrices().stream()
                .findFirst()
                .map(price -> price.getValue().toString());
    }
}
