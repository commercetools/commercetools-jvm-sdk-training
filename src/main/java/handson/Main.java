package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.*;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.commands.OrderDeleteCommand;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.SetCustomType;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.attributes.AttributeDefinitionBuilder;
import io.sphere.sdk.products.attributes.StringType;
import io.sphere.sdk.products.attributes.BooleanType;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;
import static io.sphere.sdk.models.TextInputHint.*;
import static io.sphere.sdk.utils.SetUtils.asSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;

public class Main {
    public static final String PRODUCT_TYPE_KEY = RandomStringUtils.randomAlphanumeric(10);
    public static final String ORDER_WITH_FRAUD_SCORE_TYPE_KEY = RandomStringUtils.randomAlphanumeric(10);
    public static final String FRAUD_SCORE_FIELD = "fraudScore";

    public static void main(String[] args) throws IOException {
        final Properties prop = loadCommercetoolsPlatformProperties();
        final String projectKey = prop.getProperty("projectKey");
        final String clientId = prop.getProperty("clientId");
        final String clientSecret = prop.getProperty("clientSecret");
        final SphereClientConfig clientConfig = SphereClientConfig.of(projectKey, clientId, clientSecret);

        try(final SphereClient client = SphereClientFactory.of().createClient(clientConfig)) {

            /**
             * TASKS:
             *
             * - Add attribute values for the products:
             *      - Modify "createProduct" method to pass `String color` and `boolean handmade`
             *      - Add these values to the master variant draft
             *
             * - Search products, by the following criteria:
             *      - Sort them by attribute color ascending
             *      - Additionally sort them by price descending (multi sorting)
             *      - Try using fulltext search and search for a value
             *
             * - Display information from master variant products (remember: staged versions!):
             *      - Display SKU
             *      - Display price
             *      - Display attribute "color" (string) and "handmade" (boolean)
             */


            // Fetch first tax category we find for simplicity
            final TaxCategory taxCategory = execute(client, TaxCategoryQuery.of()).head()
                    .orElseThrow(() -> new RuntimeException("Missing a tax category"));

            // Done only once at the creation of the project (usually on import process)
            final ProductType productType = createProductType(client);
            final Product shirt = createProduct(client, productType, taxCategory, "shirt");
            final Product pants = createProduct(client, productType, taxCategory, "pants");

            // Done only once at creation of the project (usually on import process)
            final Type typeForCarts = createTypeForExtendedCarts(client);
            System.err.println("Created typeForCart for extending carts with fraud score");
            System.err.println(typeForCarts);

            // We create a cart at some moment, e.g. when the customer logs in
            final Cart cart = createCart(client);
            printCart(cart, "Empty cart created");

            final String sessionCartId = cart.getId();

            // Many pages are clicked...

            // First we fetch our cart from the session
            final Cart cartFetchedFromSession = fetchCartById(client, sessionCartId);

            // When clicked on "add to cart" we add a product to the cart, which contains a non-customized price
            final int quantity = 3;
            final String productId = shirt.getId(); // Data coming from the request
            final Integer variantId = shirt.getMasterData().getStaged().getMasterVariant().getId(); // Data coming from the request
            final Cart cartWithLineItem = addProductToCart(client, cartFetchedFromSession, productId, variantId, quantity);
            printCart(cartWithLineItem, "Added product to cart");

            // On shipping selection, introduced at least a country for shipping
            final CountryCode country = CountryCode.DE;
            final Cart cartWithShippingAddress = setShippingAddress(client, cartWithLineItem, country);

            // When it is confirmed and paid, we create the order
            final Order order = createOrderFromCart(client, cartWithShippingAddress);
            printCart(order, "Created order");

            // On the other hand, the microservice listens to an order created message...
            final double fraudScore = 7; // Data coming from the fraud detection external system
            final Order orderWithFraudScore = setFraudScore(client, order, fraudScore);
            printCart(orderWithFraudScore, "Updated order with fraud score");

            // Clean up project to avoid conflicts for next iteration
            cleanUpProject(client, productType, asList(shirt, pants), orderWithFraudScore, typeForCarts);
        }
    }

    private static Order setFraudScore(final SphereClient client, final Order order, final double fraudScore) {
        final Map<String, Object> values = new HashMap<>();
        values.put(FRAUD_SCORE_FIELD, fraudScore);
        final SetCustomType setCustomType = SetCustomType.ofTypeKeyAndObjects(ORDER_WITH_FRAUD_SCORE_TYPE_KEY, values);
        return execute(client, OrderUpdateCommand.of(order, setCustomType));
    }

    private static void printCart(final CartLike<?> cartLike, final String message) {
        System.err.println("");
        System.err.println(message + ":");
        System.err.println("Line items:");
        cartLike.getLineItems().forEach(System.err::println);
        if (cartLike.getCustom() != null) {
            Optional.ofNullable(cartLike.getCustom().getFieldAsLong(FRAUD_SCORE_FIELD))
                    .ifPresent(fraudScore -> System.err.println("Fraud score: " + fraudScore));
        }
    }

    private static Cart createCart(final SphereClient client) {
        return execute(client, CartCreateCommand.of(CartDraft.of(EUR)));
    }

    private static Cart fetchCartById(final SphereClient client, final String cartId) {
        return execute(client, CartByIdGet.of(cartId));
    }

    private static Cart addProductToCart(final SphereClient client, final Cart cart, final String productId,
                                         final int variantId, final int quantity) {
        return execute(client, CartUpdateCommand.of(cart, AddLineItem.of(productId, variantId, quantity)));
    }

    private static Cart setShippingAddress(final SphereClient client, final Cart cart, final CountryCode country) {
        final Address address = Address.of(country);
        return execute(client, CartUpdateCommand.of(cart, SetShippingAddress.of(address)));
    }

    private static Order createOrderFromCart(final SphereClient client, final Cart cart) {
        return execute(client, OrderFromCartCreateCommand.of(cart));
    }

    private static Type createTypeForExtendedCarts(final SphereClient client) {
        // The type is only created once per project, i.e. this doesn't need to be here in a real example
        final LocalizedString cartTypeName = LocalizedString.of(ENGLISH, "Cart with fraud score");
        final TypeDraft typeDraft = TypeDraftBuilder.of(ORDER_WITH_FRAUD_SCORE_TYPE_KEY, cartTypeName, asSet("order"))
                .fieldDefinitions(singletonList(
                        FieldDefinition.of(io.sphere.sdk.types.NumberType.of(), FRAUD_SCORE_FIELD, LocalizedString.of(ENGLISH, "Fraud score"), false, SINGLE_LINE)
                )).build();
        return execute(client, TypeCreateCommand.of(typeDraft));
    }

    private static Properties loadCommercetoolsPlatformProperties() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        return prop;
    }

    private static Product createProduct(final SphereClient client, final ProductType productType,
                                         final TaxCategory taxCategory, final String name) {
        final LocalizedString localizedName = LocalizedString.of(ENGLISH, name);
        final LocalizedString randomSlug = LocalizedString.of(ENGLISH, RandomStringUtils.randomAlphanumeric(10));
        final ProductVariantDraft masterVariant = ProductVariantDraftBuilder.of()
                .price(PriceDraft.of(BigDecimal.valueOf(RandomUtils.nextLong(1, 50)), EUR))
                .build();
        final ProductDraft draft = ProductDraftBuilder.of(productType, localizedName, randomSlug, masterVariant)
                .taxCategory(taxCategory)
                .build();
        return execute(client, ProductCreateCommand.of(draft));
    }

    private static ProductType createProductType(final SphereClient client) {
        final ProductTypeDraft draft = ProductTypeDraft.of(PRODUCT_TYPE_KEY, PRODUCT_TYPE_KEY, "", asList(
                AttributeDefinitionBuilder.of("color", LocalizedString.of(ENGLISH, "Color"), StringType.of()).build(),
                AttributeDefinitionBuilder.of("handmade", LocalizedString.of(ENGLISH, "Handmade"), BooleanType.of()).build()));
        return execute(client, ProductTypeCreateCommand.of(draft));
    }

    private static void cleanUpProject(final SphereClient client, final ProductType productType, final List<Product> products,
                                       final Order order, final Type typeForCarts) {
        execute(client, OrderDeleteCommand.of(order));
        if (order.getCart() != null) {
            final Cart cart = execute(client, CartByIdGet.of(order.getCart().getId()));
            execute(client, CartDeleteCommand.of(cart));
        }
        execute(client, TypeDeleteCommand.of(typeForCarts));
        products.forEach(p -> execute(client, ProductDeleteCommand.of(p)));
        execute(client, ProductTypeDeleteCommand.of(productType));
    }

    private static <T> T execute(final SphereClient client, final SphereRequest<T> sphereRequest) {
        return client.execute(sphereRequest).toCompletableFuture().join();
    }
}
