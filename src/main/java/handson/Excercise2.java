package handson;

import handson.impl.CustomerService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.Utils.createSphereClient;


/**
 * Registers a new customer.
 */
public class Excercise2 {
    private final static Logger LOG = LoggerFactory.getLogger(Excercise2.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);

            final String email = String.format("%s@example.com", UUID.randomUUID().toString());

            final CompletableFuture<CustomerToken> customerTokenResult = customerService.createCustomer(email, "password").thenComposeAsync(
                    customerSignInResult -> customerService.createEmailVerificationToken(customerSignInResult.getCustomer(), 5))
                    .toCompletableFuture();
            final CustomerToken customerToken = customerTokenResult.get();

            final CompletableFuture<Customer> verifyEmailResult = customerService.verifyEmail(customerToken)
                    .toCompletableFuture();
            final Customer customer = verifyEmailResult.get();

            LOG.info("Registered customer {}", customer);
        }
    }
}
