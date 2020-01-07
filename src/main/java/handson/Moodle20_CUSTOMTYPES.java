package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;



public class Moodle20_CUSTOMTYPES {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle20_CUSTOMTYPES.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // Which fields will be used?
            List<FieldDefinition> definitions = null;

            // Which types will be extended?
            Set<String> resourceTypeIds = null;

            LOG.info("Custom Type info {}",
                ""
            );

        }
    }
}
