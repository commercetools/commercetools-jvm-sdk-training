package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.extensions.*;
import io.sphere.sdk.extensions.commands.ExtensionCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


public class Moodle24_APIEXTENSION {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle24_APIEXTENSION.class);


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {


            HttpDestination httpCall = new HttpDestination() {
                @Override
                public String getUrl() {
                    return "http://www.test.com";
                }

                @Override
                public HttpDestinationAuthentication getAuthentication() {
                    return null;
                }
            };

            Trigger trigger = new Trigger() {
                @Override
                public ExtensionResourceType getResourceTypeId() {
                    return ExtensionResourceType.CART;
                }

                @Override
                public List<TriggerType> getActions() {
                    return
                            Arrays.asList(
                                TriggerType.CREATE);
                }
            };

            LOG.info("Created extension {}",
                ""
            );
        }
    }
}
