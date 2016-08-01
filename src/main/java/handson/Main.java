package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.SetCustomType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static handson.Utils.createSphereClient;
import static handson.Utils.printCart;
import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;

public class Main extends ProjectInitialization {

    public static void main(String[] args) throws IOException {
        try (final BlockingSphereClient client = createSphereClient()) {

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

            // Set up project with some products
            setUpProject(client);

            // The online shop creates an order
            final Order order = onlineShopSide(client);

            // The microservice listens to orders and sets fraud score
            final Order orderWithFraudScore = microServiceSide(client, order);

            // Clean up project to avoid conflicts for next execution
            cleanUpProject(client, orderWithFraudScore);
        }
    }

    /**
     * What the online shop does.
     * In particular:
     * - Creates a cart
     * - Fills the cart with line items
     * - Sets the shipping address of the cart
     * - Places an order with the cart
     * @param client CTP client
     * @return the created order at the end of the online shop execution
     */
    private static Order onlineShopSide(final BlockingSphereClient client) {
        // We create a cart at some moment, e.g. when the customer logs in
        final Cart cart = createCart(client);
        printCart(cart, "Empty cart created");

        // We keep the cart ID (typically in session) for future requests
        final String sessionCartId = cart.getId();

        // Many pages are clicked...

        // First we fetch our cart from the session
        final Cart cartFetchedFromSession = fetchCartById(client, sessionCartId);

        // When the user clicks on "add to cart", we receive the following data from the request:
        final int quantity = 3;
        final String productId = shirt.getId();
        final Integer variantId = shirt.getMasterData().getStaged().getMasterVariant().getId();

        // Then we add a product to the cart, which contains a non-customized price
        final Cart cartWithLineItem = addProductToCart(client, cartFetchedFromSession, productId, variantId, quantity);
        printCart(cartWithLineItem, "Added product to cart");

        // When the user introduces the shipping address, we receive the country for shipping (minimum info necessary):
        final Address shippingAddress = Address.of(CountryCode.DE);

        // Then we set the shipping address to the cart
        final Cart cartWithShippingAddress = setShippingAddress(client, cartWithLineItem, shippingAddress);

        // Checkout process continues...

        // When it is confirmed and paid, we create the order
        final Order order = createOrderFromCart(client, cartWithShippingAddress);
        printCart(order, "Created order");
        return order;
    }

    /**
     * What the microservice is doing.
     * In particular:
     * - Sets the fraud score to the order
     * @param client CTP client
     * @param order the recently created Order (represents the order obtained via Messages)
     * @return the updated order with the Fraud Score set
     */
    private static Order microServiceSide(final BlockingSphereClient client, final Order order) {
        // On the other hand, the microservice listens to an order created message...

        // When a new order is detected, we request a fraud detection external system for a score:
        final double fraudScore = 7;

        // Then we update the cart with the given fraud score
        final Order orderWithFraudScore = setFraudScore(client, order, fraudScore);
        printCart(orderWithFraudScore, "Updated order with fraud score");
        return orderWithFraudScore;
    }

    /**
     * Sets the given fraud score to the Order.
     * @param client CTP client
     * @param order the order to which the fraud score should be set
     * @param fraudScore the fraud score for the order
     * @return the updated order with the given fraud score set
     */
    private static Order setFraudScore(final BlockingSphereClient client, final Order order, final double fraudScore) {
        final Map<String, Object> values = new HashMap<>();
        values.put(FRAUD_SCORE_FIELD, fraudScore);
        final SetCustomType setCustomType = SetCustomType.ofTypeKeyAndObjects(ORDER_WITH_FRAUD_SCORE_TYPE_KEY, values);
        final OrderUpdateCommand updateCommand = OrderUpdateCommand.of(order, setCustomType);
        return client.executeBlocking(updateCommand);
    }

    /**
     * Creates an empty Cart.
     * @param client CTP client
     * @return the created cart
     */
    private static Cart createCart(final BlockingSphereClient client) {
        final CartCreateCommand createCommand = CartCreateCommand.of(CartDraft.of(EUR));
        return client.executeBlocking(createCommand);
    }

    /**
     * Fetches the Cart identified by the given ID.
     * @param client CTP client
     * @param cartId identifies the cart
     * @return the cart associated with the given ID
     */
    private static Cart fetchCartById(final BlockingSphereClient client, final String cartId) {
        final CartByIdGet fetchQuery = CartByIdGet.of(cartId);
        return client.executeBlocking(fetchQuery);
    }

    /**
     * Adds the given Product/Variant combination with the given quantity to the Cart.
     * @param client CTP client
     * @param cart the cart to which the product should be added
     * @param productId the identifier of the product
     * @param variantId the identifier of the variant
     * @param quantity the amount of product units to add
     * @return the updated cart with the product as a line item
     */
    private static Cart addProductToCart(final BlockingSphereClient client, final Cart cart, final String productId,
                                         final int variantId, final int quantity) {
        final CartUpdateCommand updateCommand = CartUpdateCommand.of(cart, AddLineItem.of(productId, variantId, quantity));
        return client.executeBlocking(updateCommand);
    }

    /**
     * Sets the given shipping address to the Cart.
     * @param client CTP client
     * @param cart the cart to which the shipping address is set
     * @param shippingAddress the shipping address to associate to the cart
     * @return the updated cart with the shipping address
     */
    private static Cart setShippingAddress(final BlockingSphereClient client, final Cart cart, final Address shippingAddress) {
        final CartUpdateCommand updateCommand = CartUpdateCommand.of(cart, SetShippingAddress.of(shippingAddress));
        return client.executeBlocking(updateCommand);
    }

    /**
     * Create an Order out of the given Cart.
     * @param client CTP client
     * @param cart the cart to be transformed into an order
     * @return the created Order
     */
    private static Order createOrderFromCart(final BlockingSphereClient client, final Cart cart) {
        final OrderFromCartCreateCommand createCommand = OrderFromCartCreateCommand.of(cart);
        return client.executeBlocking(createCommand);
    }
}
