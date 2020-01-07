package handson;

import io.sphere.sdk.client.SphereClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


public class Moodle55_SPHERECLIENT_LOGGING {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle55_SPHERECLIENT_LOGGING.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // TODO
            // 1: Log all connections the sphereclient is using
            // 2: ReUse an older token, Use a different flow
            // 3: X-correlation id setting


            // 1
            // Logging von sphereclient an/aus-schalten
            // Ich kann simulieren, dass ich den token vergesse

            // 2
            // wait for 1
            // Reuse old token to get new token

            // 3
            // wait for 1, discuss
/*
            final T password = client.execute(
                    CustomerCreateCommand.of(
                            CustomerDraftBuilder.of("mh-test", "password")
                                    .build()
                    )
                            .httpRequestIntent().plusHeader("X-Correlation-ID", "Be sure to set it correctly!")
                    //.toHttpRequest("https://api.sphere.io")
            ).toCompletableFuture().get();
*/

        }
    }
}
