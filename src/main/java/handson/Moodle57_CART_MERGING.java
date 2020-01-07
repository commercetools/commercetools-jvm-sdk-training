package handson;

import com.neovisionaries.i18n.CountryCode;
import handson.impl.CartService;
import handson.impl.CustomerService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerSignInCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;
import static io.sphere.sdk.carts.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static io.sphere.sdk.carts.AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART;


public class Moodle57_CART_MERGING {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle57_CART_MERGING.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);

            // todo cart merging
            // complete, test

            // TODO Create a cart for this customer, add products
            //
            final CustomerSignInResult customerSignInResult =
                    customerService.createCustomer("me@me", "password")
                        .toCompletableFuture().get();
            Customer customer = customerSignInResult.getCustomer();


            // TODO: Add products to the anon. cart, test after merging
            //
            Cart anonymousCart = cartService.createAnonymousCart()
                    .toCompletableFuture().get();
            CustomerSignInCommand
                    .of(customer.getEmail(), "password", anonymousCart.getId())
                    .withAnonymousCartSignInMode(MERGE_WITH_EXISTING_CUSTOMER_CART);

            // TODO: Add products to the anon. cart, test after abondoning new cart
            //
            CustomerSignInCommand cmd = CustomerSignInCommand
                    .of(customer.getEmail(), "password", anonymousCart.getId())
                    .withAnonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART);


        }
    }
}
