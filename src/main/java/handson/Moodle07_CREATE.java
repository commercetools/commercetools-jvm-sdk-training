package handson;

import com.neovisionaries.i18n.CountryCode;
import handson.impl.CustomerService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.CustomerToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;

/**
 * Registers a new customer.
 *
 * See:
 *  TODO Task07.1 {@link CustomerService#createCustomer(String, String)}
 *  TODO Task07.2 {@link CustomerService#createEmailVerificationToken(Customer, Integer)}
 *  TODO Task07.3 {@link CustomerService#verifyEmail(CustomerToken)}
 */
public class Moodle07_CREATE {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle07_CREATE.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);

            // TODO: Create a customer with email and password
            LOG.info("Registered customer {}",
                    customerService.createCustomer("mh1@example.com", "password")
                            .toCompletableFuture().get()
            );

            // TODO: Create a customer with email, password, key, default shipping address based on countryCode
            LOG.info("Registered customer {}",
                    customerService.createCustomer("mh2@example.com",
                                                "password",
                                                    "mh2-customer",
                                                "michael",
                                                "hartwig",
                                                            CountryCode.DE
                    )
                    .toCompletableFuture().get()
            );


        }
    }
}
