package handson;

import io.sphere.sdk.client.JsonNodeSphereRequest;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;



public class Moodle12_JSNODESPHERE {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle12_JSNODESPHERE.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // TODO: Use the Category Recommendation API
            //
            LOG.info("Categories from name: {}",
                    ""
            );

            LOG.info("Categories from images: {}",
                    ""
            );

        }
    }
}
