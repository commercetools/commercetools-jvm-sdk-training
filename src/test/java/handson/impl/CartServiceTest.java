package handson.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import io.sphere.sdk.cartdiscounts.CartDiscount;
import io.sphere.sdk.cartdiscounts.CartDiscountDraft;
import io.sphere.sdk.cartdiscounts.CartDiscountDraftBuilder;
import io.sphere.sdk.cartdiscounts.CartDiscountValue;
import io.sphere.sdk.cartdiscounts.CartPredicate;
import io.sphere.sdk.cartdiscounts.LineItemsTarget;
import io.sphere.sdk.cartdiscounts.commands.CartDiscountCreateCommand;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.discountcodes.DiscountCodeDraft;
import io.sphere.sdk.discountcodes.DiscountCodeDraftBuilder;
import io.sphere.sdk.discountcodes.commands.DiscountCodeCreateCommand;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;

public class CartServiceTest extends BaseTest {
	
	private static final String DISCOUNT_CODE = "CHRISTMAS17";
	
    private CustomerService customerService;
    private ProductQueryService productQueryService;
    private CartService cartService;
    
    @Before
    public void setup() throws IOException {
        super.setup();
        
    	CartDiscountDraft cartDiscount = CartDiscountDraftBuilder.of(LocalizedString.of(Locale.US, "cartDiscount"), 
    		CartPredicate.of("1 = 1"), CartDiscountValue.ofRelative(10), 
    		LineItemsTarget.ofAll(), "0.5", true).build();
    	CompletableFuture<CartDiscount> cartDiscountFuture = client()
    		.execute(CartDiscountCreateCommand.of(cartDiscount))
    		.toCompletableFuture();
    	
    	try {
    		DiscountCodeDraft discountCode = DiscountCodeDraftBuilder.of(DISCOUNT_CODE, 
				cartDiscountFuture.get())
				.build();
			client()
				.execute(DiscountCodeCreateCommand.of(discountCode))
    			.toCompletableFuture()
    			.get();
		} catch (InterruptedException | ExecutionException e) {
			/*
			 * Do nothing
			 */
		}
    	
        
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

        final CompletableFuture<Cart> discountedResult = cartService.addDiscountToCart(DISCOUNT_CODE, updatedCart)
                .toCompletableFuture();
        final Cart discountedCart = discountedResult.get();
        assertThat(discountedCart.getDiscountCodes()).hasSize(1);
    }
}
