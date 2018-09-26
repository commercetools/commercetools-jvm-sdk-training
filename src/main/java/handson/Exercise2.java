package handson;

import handson.impl.CustomerService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.CustomerToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;

/**
 * Registers a new customer.
 *
 * See:
 *  TODO 2.1 {@link CustomerService#createCustomer(String, String)}
 *  TODO 2.2 {@link CustomerService#createEmailVerificationToken(Customer, Integer)}
 *  TODO 2.3 {@link CustomerService#verifyEmail(CustomerToken)}
 */
public class Exercise2 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise2.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);

            final String email = String.format("%s@example.com", UUID.randomUUID().toString());

            final CompletionStage<CustomerSignInResult> customerCreationResult = customerService.createCustomer(email, "password");

            final CompletableFuture<CustomerToken> customerTokenResult = customerCreationResult
                .thenComposeAsync(customerSignInResult ->
                    customerService.createEmailVerificationToken(customerSignInResult.getCustomer(), 5))
                .toCompletableFuture();
            final CustomerToken customerToken = customerTokenResult.get();

            final CompletableFuture<Customer> verifyEmailResult = customerService.verifyEmail(customerToken)
                    .toCompletableFuture();
            final Customer customer = verifyEmailResult.get();

            LOG.info("Registered customer {}", customer);
        }
    }
}
