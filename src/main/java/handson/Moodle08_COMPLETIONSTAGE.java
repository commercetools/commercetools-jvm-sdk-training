package handson;

import handson.impl.CustomerService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerToken;
import io.sphere.sdk.customers.queries.CustomerByKeyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
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
public class Moodle08_COMPLETIONSTAGE {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle08_COMPLETIONSTAGE.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);


            // TODO: Get and verify a selected customer
            // Get your customer via key (preferred) or id
            //
            LOG.info("Registered customer {}",
                    client.execute(CustomerByKeyGet.of("mh2-customer"))
                            .thenComposeAsync(customer ->
                                    customerService.createEmailVerificationToken(customer, 5))
                            .thenComposeAsync(customerService::verifyEmail)
                            .toCompletableFuture().get()
            );


            // TODO: Handle exceptions, CompletionStage
            //
                    client.execute(CustomerByKeyGet.of("WRONG-KEY"))
                            .thenComposeAsync(customer ->
                                    customerService.createEmailVerificationToken(customer, 5))
                            .thenComposeAsync(customerService::verifyEmail)
                            .exceptionally(throwable -> {
                                LOG.info("I am not existing");
                                return  null;
                            })
                            .toCompletableFuture().get();


            // TODO: Handle exceptions, Optionals, Either (Java 9+)
            //
                    Optional<Customer> customerFetchedViaKey = Optional.ofNullable(
                            client.execute(CustomerByKeyGet.of("WRONG-KEY"))
                                                                    .toCompletableFuture().get()
                    );
                    if (!customerFetchedViaKey.isPresent()) {
                        LOG.info("I am not existing");
                    }
                    customerFetchedViaKey.ifPresent(customer -> {
                        LOG.info("I know that I exist.");
                        try {
                            customerService.createEmailVerificationToken(customer, 5)
                            .thenComposeAsync(customerService::verifyEmail)
                            .toCompletableFuture().get();
                        }
                        catch (Exception e) {}
                    });

        }
    }
}
