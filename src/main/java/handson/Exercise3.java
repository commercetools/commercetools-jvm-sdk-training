package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionByKeyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


/**
 * Create a cart for a customer.
 *
 * See:
 *  TODO 3.1 {@link CartService#createCart(Customer)}
 *  TODO 3.2 {@link CartService#addProductToCart(ProductProjection, Cart)}
 */
public class Exercise3 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise3.class);

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

            final ProductProjection productProjection = client.execute(ProductProjectionByKeyGet.ofCurrent("123"))
                                                              .toCompletableFuture()
                                                              .join();


            final CompletableFuture<Cart> addToCartResult = cartService.addProductToCart(productProjection, cart)
                                                                       .toCompletableFuture();

            final Cart cartAfterUpdate = addToCartResult.get();
            LOG.info("Updated cart with product {}", cartAfterUpdate);
        }
    }
}
