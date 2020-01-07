package handson;

import handson.impl.*;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.queries.ChannelByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.queries.CustomerByKeyGet;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.subscriptions.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Create a subscription for OrderCreatedMessages.
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 *  TODO 9.1 {@link SubscriptionService#createSqsSubscription()}
 *  TODO 9.2 {@link SubscriptionService#deleteSqsSubscription(Subscription)}
 */
public class Moodle23_SUBSCRIPTION {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle23_SUBSCRIPTION.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);
            final OrderService orderService = new OrderService(client);
            final SubscriptionService subscriptionService = new SubscriptionService(client, "eu-west-1", "https://sqs.eu-west-1.amazonaws.com/501843469210/tut_mko");


            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            Channel channel = null;

            // TODO: Create subscription
            //
            final Subscription subscription = null;

            LOG.info("Created order {}",
                    client.execute(CustomerByKeyGet.of("mh2-customer"))
                            .thenComposeAsync(cartService::createCart)
                            .thenComposeAsync(cart -> cartService.addProductToCartBySkusAndChannel(cart, channel,
                                    "987"))
                            .thenComposeAsync(orderService::createOrder)
                            .thenComposeAsync(order -> orderService.changeState(order, OrderState.COMPLETE))
                            .toCompletableFuture().get()
            );

            subscriptionService.deleteSqsSubscription(subscription)
                    .toCompletableFuture().get();


        }
    }
}
