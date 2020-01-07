package handson;

import handson.impl.ClientService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import io.sphere.sdk.shippingmethods.ShippingMethod;
import io.sphere.sdk.shippingmethods.queries.ShippingMethodByIdGet;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.queries.TaxCategoryByKeyGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;

/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createSphereClient()}
 */
public class Moodle06_GET {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle06_GET.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // TODO: GET the project information
            LOG.info("Project info {}",
                    "TODO: Print project information"
            );

            // TODO: GET a tax category via key
            LOG.info("TaxCategory info {}",
                    ""
            );

            // TODO: GET a shipping method via id
            LOG.info("ShippingMethod info {}",
                    ""
            );

        }
    }
}
