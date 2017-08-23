package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.*;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerCreateEmailTokenCommand;
import io.sphere.sdk.customers.commands.CustomerVerifyEmailCommand;

import java.util.concurrent.CompletionStage;

public class CustomerService {
    private final SphereClient client;

    public CustomerService(final SphereClient client) {
        this.client = client;
    }

    public CompletionStage<Customer> verifyEmail(final CustomerToken customerToken) {
        return client.execute(CustomerVerifyEmailCommand.ofCustomerToken(customerToken));
    }

    public CompletionStage<CustomerToken> register(final String email, final String password) {
        return create(email, password).thenComposeAsync(
                customerSignInResult -> createEmailVerificationToken(customerSignInResult.getCustomer()));
    }

    public CompletionStage<CustomerSignInResult> create(final String email, final String password) {
        final CustomerDraftDsl newCustomer = CustomerDraftBuilder.of(email, password)
                .build();

        return client.execute(CustomerCreateCommand.of(newCustomer));
    }

    public CompletionStage<CustomerToken> createEmailVerificationToken(final Customer customer) {
        return client.execute(CustomerCreateEmailTokenCommand.of(customer, 5));
    }
}
