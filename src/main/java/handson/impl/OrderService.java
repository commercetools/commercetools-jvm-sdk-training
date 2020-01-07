package handson.impl;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangeOrderState;

import java.util.concurrent.CompletionStage;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class    OrderService extends AbstractService {

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

        return
                client.execute(
                        OrderFromCartCreateCommand.of(cart)
                );
    }

    /**
     * Changes the state of the given order.
     *
     * @param order the order
     * @param state the new state
     * @return the order state change completion stage
     */
    public CompletionStage<Order> changeState(final Order order, final OrderState state) {

        return
                client.execute(
                        OrderUpdateCommand.of(order, ChangeOrderState.of(state)
                        )
                );
    }
}
