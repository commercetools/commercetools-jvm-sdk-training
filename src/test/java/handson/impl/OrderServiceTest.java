package handson.impl;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest extends BaseTest {
    private CustomerService customerService;
    private ProductQueryService productQueryService;
    private CartService cartService;
    private OrderService orderService;

    @Before
    public void setup() throws IOException {
        super.setup();
        customerService = new CustomerService(client());
        productQueryService = new ProductQueryService(client());
        cartService = new CartService(client());
        orderService = new OrderService(client());
    }

    @Test
    public void createOrder() throws ExecutionException, InterruptedException {
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

        final CompletableFuture<Cart> addToCartResult = cartService.addProductToCart(productProjection, cart).toCompletableFuture();

        final Cart updatedCart = addToCartResult.get();

        final CompletableFuture<Order> orderCompletionResult = orderService.createOrder(updatedCart).thenComposeAsync(
                order -> orderService.changeState(order, OrderState.COMPLETE))
                .toCompletableFuture();

        final Order order = orderCompletionResult.get();
        assertThat(order.getState()).isEqualTo(OrderState.COMPLETE);
    }

}
