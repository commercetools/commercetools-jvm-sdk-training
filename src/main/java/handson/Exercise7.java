package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.OrderService;
import handson.impl.ProductQueryService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 *  TODO 7.1 {@link OrderService#changeState(Order, OrderState)}
 */
public class Exercise7 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise7.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);
            final ProductQueryService productQueryService = new ProductQueryService(client);
            final OrderService orderService = new OrderService(client);

            final String email = String.format("%s@example.com", UUID.randomUUID().toString());

            final CompletableFuture<Cart> cartCreationResult = customerService.createCustomer(email, "password")
                    .thenComposeAsync(customerSignInResult -> cartService.createCart(customerSignInResult.getCustomer()))
                    .toCompletableFuture();

            final Cart cart = cartCreationResult.get();

            final CompletableFuture<PagedQueryResult<ProductProjection>> productsOnSaleResult =
                    productQueryService.findProductsWithCategory(Locale.ENGLISH, "Sale")
                            .toCompletableFuture();

            final PagedQueryResult<ProductProjection> productProjectionPagedQueryResult = productsOnSaleResult.get();
            final ProductProjection productProjection = productProjectionPagedQueryResult.getResults().get(0);

            final CompletableFuture<Cart> addToCartResult = cartService.addProductToCart(productProjection, cart)
                                                                       .toCompletableFuture();

            final Cart updatedCart = addToCartResult.get();

            final CompletableFuture<Order> orderCreationResult = orderService.createOrder(updatedCart)
                                                                             .toCompletableFuture();
            final Order order = orderCreationResult.get();

            LOG.info("Created order {}", order);

            final CompletableFuture<Order> orderChangeResult = orderService.changeState(order, OrderState.CANCELLED)
                                                                           .toCompletableFuture();
            final Order updatedOrder = orderChangeResult.get();

            LOG.info("Updated order {}", updatedOrder);
        }
    }
}
