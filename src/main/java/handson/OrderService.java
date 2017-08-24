package handson;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangeOrderState;

import java.util.concurrent.CompletionStage;

public class OrderService extends AbstractService {

    public OrderService(SphereClient client) {
        super(client);
    }

    public CompletionStage<Order> createOrder(final Cart cart) {
        return client.execute(OrderFromCartCreateCommand.of(cart));
    }

    public CompletionStage<Order> changeState(final Order order, final OrderState state) {
        return client.execute(OrderUpdateCommand.of(order, ChangeOrderState.of(state)));
    }
}
