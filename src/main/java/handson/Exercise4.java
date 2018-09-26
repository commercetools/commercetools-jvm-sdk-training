package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.ProductQueryService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.SphereClient;
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
 * Create a cart for a customer and add a product to it.
 *
 * See:
 *  TODO 4.1 {@link ProductQueryService#findCategory(Locale, String)}
 *  TODO 4.2 {@link ProductQueryService#withCategory(Category)}
 *  TODO 4.3 {@link ProductQueryService#findProductsWithCategory(Locale, String)}
 */
public class Exercise4 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise4.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);
            final ProductQueryService productQueryService = new ProductQueryService(client);

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

            LOG.info("Found product under category 'sale' {}", productProjection);

            // Get updated cart after adding product to cart in task 3.2.
            final CompletableFuture<Cart> addToCartResult = cartService.addProductToCart(productProjection, cart)
                                                                       .toCompletableFuture();

            final Cart updatedCart = addToCartResult.get();

            LOG.info("Cart with added product {}", updatedCart);
        }
    }
}
