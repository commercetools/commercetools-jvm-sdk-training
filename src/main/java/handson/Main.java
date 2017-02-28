package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.orders.Order;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.Commands.*;
import static handson.Project.*;
import static handson.Utils.createSphereClient;

public class Main {

    private static final Address address = AddressBuilder.of(CountryCode.DE).build();

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        try (final BlockingSphereClient client = createSphereClient()){

            setUpProject(client);

            final Order order = onlineShop(client);

            // .. the order in a microservice, e.g. send confirmation email to the customer.

            cleanUpProject(client, product, taxCategory, cart, order);
        }
    }

    /**
     * Online ship side:
     * - queries a cart, or creates one if none is available
     * - add product to cart
     * - sets a shipping address to cart
     * - creates order from cart
     * @param client CTP client
     * @return The created order
     */
    private static Order onlineShop(final BlockingSphereClient client){
        //TODO 3.6.2. Call the method queryFirstCart
        //System.out.println("Cart with id " + cart.getId() + " is queried/created");

        //TODO 3.7. Call addProductToCart
        //System.out.println("Product with id " + product.getId() + " is added to cart.");

        //TODO 3.8. Call setShippingAddress
        //System.out.println("Set address to cart with id " + cart.getId());

        // Checkout process continues...

        // When it is confirmed and paid, we create the order
        //TODO 3.9. Call the method createOrderFromCart
        //System.out.println("Order with id " + order.getId() + " is added to cart with id " + cart.getId());

        //Return order
        return null;
    }
}