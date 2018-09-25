package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Create a cart for a customer and add a discount to it.
 *
 * See:
 *  TODO 5.1 {@link CartService#addDiscountToCart(String, Cart)}
 */
public class Exercise5 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise5.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);

            final String email = String.format("%s@example.com", UUID.randomUUID().toString());

            final CompletableFuture<Cart> cartCreationResult = customerService.createCustomer(email, "password")
                    .thenComposeAsync(customerSignInResult -> cartService.createCart(customerSignInResult.getCustomer()))
                    .toCompletableFuture();

            final Cart cart = cartCreationResult.get();

            LOG.info("Created cart {}", cart);

            final CompletableFuture<Cart> discountedCartResult = cartService.addDiscountToCart("TESTCODE", cart)
                                                                            .toCompletableFuture();

            final Cart updatedCart = discountedCartResult.get();

            LOG.info("Updated cart with a discount {}", updatedCart);
        }
    }
}
