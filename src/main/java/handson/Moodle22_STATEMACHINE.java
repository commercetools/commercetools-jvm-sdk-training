package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.states.State;
import io.sphere.sdk.states.StateDraftBuilder;
import io.sphere.sdk.states.StateType;
import io.sphere.sdk.states.commands.StateCreateCommand;
import io.sphere.sdk.states.commands.StateUpdateCommand;
import io.sphere.sdk.states.commands.updateactions.SetTransitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static handson.impl.ClientService.createSphereClient;


public class Moodle22_STATEMACHINE {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle22_STATEMACHINE.class);


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            final State stateOrderPacked =
                    null;

            final State stateOrderShipped =
                    null;

            LOG.info("State info {}",
                    ""
            );
        }
    }
}
