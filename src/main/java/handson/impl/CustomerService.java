package handson.impl;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.*;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerCreateEmailTokenCommand;
import io.sphere.sdk.customers.commands.CustomerVerifyEmailCommand;
import io.sphere.sdk.models.Address;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class CustomerService extends AbstractService {

    public CustomerService(final SphereClient client) {
        super(client);
    }

    /**
     * Creates a new customer {@link Customer} with the given parameters.
     *
     * @param email    the customers email
     * @param password the customers password
     * @return the customer creation completion stage
     */
    public CompletionStage<CustomerSignInResult> createCustomer(final String email, final String password) {

        final CustomerDraft customerDraft;

        return
                null;
    }

    /**
     * Creates a new customer {@link Customer} with the given parameters.
     *
     * @param email    the customers email
     * @param password the customers password
     * @return the customer creation completion stage
     */
    public CompletionStage<CustomerSignInResult> createCustomer(final String email,
                                                                final String password,
                                                                final String key,
                                                                final String firstName,
                                                                final String lastName,
                                                                final CountryCode countryCode) {

        final CustomerDraft customerDraft;

        return
                null;
    }

    /**
     * Creates an email verification token for the given customer.
     * This is then used to create a password reset link.
     *
     * @param customer            the customer
     * @param timeToLiveInMinutes the time to live (in minutes) for the token
     * @return the customer token creation completion stage
     */
    public CompletionStage<CustomerToken> createEmailVerificationToken(final Customer customer, final Integer timeToLiveInMinutes) {

        return
                null;
    }

    /**
     * Verifies the customer token.
     *
     * @param customerToken the customer token
     * @return the email verification completion stage
     */
    public CompletionStage<Customer> verifyEmail(final CustomerToken customerToken) {

        return
                null;
    }
}
