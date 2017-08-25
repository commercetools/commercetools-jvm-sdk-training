package handson.impl;

import io.sphere.sdk.carts.Cart;
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

public class CartServiceTest extends BaseTest {
    private CustomerService customerService;
    private ProductQueryService productQueryService;
    private CartService cartService;

    @Before
    public void setup() throws IOException {
        super.setup();
        customerService = new CustomerService(client());
        productQueryService = new ProductQueryService(client());
        cartService = new CartService(client());
    }

    @Test
    public void createCartAddProductAndDiscount() throws ExecutionException, InterruptedException {
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
        assertThat(updatedCart.getLineItems()).hasSize(1);

        final CompletableFuture<Cart> discountedResult = cartService.addDiscountToCart("CHRISTMAS17", updatedCart)
                .toCompletableFuture();
        final Cart discountedCart = discountedResult.get();
        assertThat(discountedCart.getDiscountCodes()).hasSize(1);
    }
}
