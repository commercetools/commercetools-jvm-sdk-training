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
                    client.execute(
                            StateCreateCommand.of(
                                    StateDraftBuilder.of("OrderPacked", StateType.ORDER_STATE)
                                        .key("OrderPacked")
                                        .initial(true)
                                        .name(LocalizedString.ofEnglish("Order Packed"))
                                        .build()
                            )
                    )
                    .toCompletableFuture().get();

            final State stateOrderShipped =
                    client.execute(
                            StateCreateCommand.of(
                                    StateDraftBuilder.of("OrderShipped", StateType.ORDER_STATE)
                                            .key("OrderShipped")
                                            .initial(false)
                                            .name(LocalizedString.ofEnglish("Order Shipped"))
                                            .build()
                            )
                    )
                    .toCompletableFuture().get();

            LOG.info("State info {}",
                    client.execute(
                            StateUpdateCommand.of(stateOrderPacked,
                                    SetTransitions.of(
                                        Stream.of(
                                            State.referenceOfId(stateOrderShipped.getId())
                                        )
                                        .collect(Collectors.toSet()))
                            )
                    ).toCompletableFuture().get()
            );
        }
    }
}
