package handson;

import io.sphere.sdk.categories.queries.CategoryByKeyGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.commands.CustomerUpdateCommand;
import io.sphere.sdk.customers.commands.updateactions.SetKey;
import io.sphere.sdk.customers.queries.CustomerByIdGet;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.AddToCategory;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.queries.ProductByKeyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;



public class Moodle11_UPDATE {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle11_UPDATE.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // Day 1
            // TODO: Get your category and product
            //
            LOG.info("My category {}",
                ""
            );
            LOG.info("My product {}",
                ""
            );

            // Day 2
            // TODO: Get the product, category, assign & publish the product
            // Optimize code!!
            //
            LOG.info("My product {}",
                ""
            );


            // Do alone!
            // TODO: Add a key to a customer!
            //
            LOG.info("Customer Updated {}",
                   ""
            );


        }
    }
}
