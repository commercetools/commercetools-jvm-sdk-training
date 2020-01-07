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
                    client.execute(JsonNodeSphereRequest.of(HttpMethod.GET,
                            "/recommendations/general-categories?productName=\"black car\"",
                            null))
                    .toCompletableFuture().get()
            );

            LOG.info("Categories from images: {}",
                    client.execute(JsonNodeSphereRequest.of(HttpMethod.GET,
                            "/recommendations/general-categories?productImageUrl=https%3A%2F%2F27f39057e2c520ef562d-e965cc5b4f2ea17c6cdc007c161d738e.ssl.cf3.rackcdn.com%2FGar-HOkwvZ2E-small.jpg",
                            null))
                    .toCompletableFuture().get()
            );

        }
    }
}
