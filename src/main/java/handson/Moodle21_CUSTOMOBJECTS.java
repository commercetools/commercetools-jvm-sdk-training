package handson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customobjects.CustomObjectDraft;
import io.sphere.sdk.customobjects.commands.CustomObjectUpsertCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.json.*;

import static handson.impl.ClientService.createSphereClient;


public class Moodle21_CUSTOMOBJECTS {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle21_CUSTOMOBJECTS.class);


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            JsonObject personObject = Json.createObjectBuilder()
                    .add("name", "John")
                    .add("age", 13)
                    .add("isMarried", false)
                    .add("address",
                            Json.createObjectBuilder().add("street", "Main Street")
                                    .add("city", "New York")
                                    .add("zipCode", "11111")
                                    .build()
                    )
                    .add("phoneNumber",
                            Json.createArrayBuilder().add("00-000-0000")
                                    .add("11-111-1111")
                                    .add("11-111-1112")
                                    .build()
                    )
                    .build();
            JsonNode jsonNode = new ObjectMapper().readTree(personObject.toString());

            LOG.info("Custom Object info {}",
                    ""
            );


            // For testing: Simple call for all custom objects
            //
/*
            final JsonNode getAllCustomObjects =
                    client.execute(JsonNodeSphereRequest.of(HttpMethod.GET,
                            "/custom-objects",
                            null))
                            .toCompletableFuture().get();
            LOG.info("JSON response: {}", getAllCustomObjects);
*/

        }
    }
}
