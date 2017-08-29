package handson.impl;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.subscriptions.*;
import io.sphere.sdk.subscriptions.commands.SubscriptionCreateCommand;
import io.sphere.sdk.subscriptions.commands.SubscriptionDeleteCommand;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class SubscriptionService extends AbstractService {
    private final String region;
    private final  URI queueUrl;

    public SubscriptionService(SphereClient client, final String region, final String queueUrl) {
        super(client);
        this.region = region;
        this.queueUrl = URI.create(queueUrl);
    }

    public CompletionStage<Subscription> createSqsSubscriptin() {
        final AwsCredentials awsCredentials = AwsCredentials.ofAwsCliEnv();
        final SubscriptionDraftDsl subscriptionDraft = SubscriptionDraftBuilder.of(SqsDestination.of(awsCredentials, region, queueUrl))
                .changes(createOrderChanges())
                .build();
        final SubscriptionCreateCommand subscriptionCreateCommand = SubscriptionCreateCommand.of(subscriptionDraft);
        return client.execute(subscriptionCreateCommand);
    }

    private List<ChangeSubscription> createOrderChanges() {
        return Collections.singletonList(ChangeSubscription.of(Order.resourceTypeId()));
    }

    public CompletionStage<Subscription> deleteSqsSubscriptin(final Subscription subscription) {
        return client.execute(SubscriptionDeleteCommand.of(subscription));
    }
}