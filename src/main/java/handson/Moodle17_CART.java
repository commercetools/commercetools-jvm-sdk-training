package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.OrderService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.Recalculate;
import io.sphere.sdk.carts.commands.updateactions.SetShippingMethod;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.queries.ChannelByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.queries.CustomerByKeyGet;
import io.sphere.sdk.models.Referenceable;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.shippingmethods.queries.ShippingMethodsByCartGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 *  TODO Task17.1 {@link OrderService#changeState(Order, OrderState)}
 */
public class Moodle17_CART {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle17_CART.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);
            final OrderService orderService = new OrderService(client);


            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            Channel channel = null;


            // TODO: Perform cart operations:
            //      Get Customer, create cart, add products, add inventory mode, add custom line items,
            //      add discount codes, perform a recalculation
            // TODO: Convert cart into an order, set order status
            //
            LOG.info("Created order {}",
                ""
            );



        }
    }
}
