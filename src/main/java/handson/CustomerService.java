package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.*;
import io.sphere.sdk.customers.commands.*;

import java.util.concurrent.CompletionStage;

public class CustomerService extends AbstractService {

    public CustomerService(final SphereClient client) {
        super(client);
    }

    public CompletionStage<Customer> verifyEmail(final CustomerToken customerToken) {
        return client.execute(CustomerVerifyEmailCommand.ofCustomerToken(customerToken));
    }

    public CompletionStage<CustomerToken> register(final String email, final String password) {
        return createCustomer(email, password).thenComposeAsync(
                customerSignInResult -> createEmailVerificationToken(customerSignInResult.getCustomer()));
    }

    public CompletionStage<CustomerSignInResult> createCustomer(final String email, final String password) {
        final CustomerDraftDsl newCustomer = CustomerDraftBuilder.of(email, password)
                .build();

        return client.execute(CustomerCreateCommand.of(newCustomer));
    }

    public CompletionStage<CustomerToken> createEmailVerificationToken(final Customer customer) {
        return client.execute(CustomerCreateEmailTokenCommand.of(customer, 5));
    }


    public CompletionStage<CustomerToken> createPasswordResetToken(final String email) {
        return client.execute(CustomerCreatePasswordTokenCommand.of(email));
    }

    public CompletionStage resetPassword(final CustomerToken passwordResetToken, final String newPassword) {
        return client.execute(CustomerPasswordResetCommand.ofTokenAndPassword(passwordResetToken.getValue(), newPassword));
    }

}
