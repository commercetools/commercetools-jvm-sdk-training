package intern;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereRequest;

import java.util.concurrent.CompletionStage;

public interface Client extends SphereClient {
    @Override
    void close();

    @Override
    <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest);

    <T> T complete(final SphereRequest<T> sphereRequest);
}
