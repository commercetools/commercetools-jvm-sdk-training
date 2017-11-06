package handson.impl;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;

import java.util.concurrent.CompletionStage;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService extends AbstractService {

    public OrderService(SphereClient client) {
        super(client);
    }

    /**
     * Creates a new order from the given cart.
     *
     * @param cart the cart
     * @return the order creation completion stage
     */
    public CompletionStage<Order> createOrder(final Cart cart) {
        // TODO 6.1 Create a new order from cart
        return null;
    }

    /**
     * Changes the state of the given order.
     *
     * @param order the order
     * @param state the new state
     * @return the order state change completion stage
     */
    public CompletionStage<Order> changeState(final Order order, final OrderState state) {
        // TODO 7.1 Change the state of an order
        return null;
    }
}
