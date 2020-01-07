package handson.impl;

import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerToken;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerServiceTest extends BaseTest {
    private CustomerService customerService;

    @Before
    public void setup() throws IOException {
        super.setup();
        customerService = new CustomerService(client());
    }

    @Test
    public void registerCustomer() throws ExecutionException, InterruptedException {
        final String email = String.format("%s@example.com", UUID.randomUUID().toString());

        final CompletableFuture<CustomerToken> customerTokenResult = customerService.createCustomer(email, "password").thenComposeAsync(
                customerSignInResult -> customerService.createEmailVerificationToken(customerSignInResult.getCustomer(), 5))
                .toCompletableFuture();
        final CustomerToken customerToken = customerTokenResult.get();
        assertThat(customerToken).isNotNull();

        final CompletableFuture<Customer> verifyEmailResult = customerService.verifyEmail(customerToken)
                .toCompletableFuture();
        final Customer customer = verifyEmailResult.get();
        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo(email);
    }
}
