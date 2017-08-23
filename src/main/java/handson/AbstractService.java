package handson;

import io.sphere.sdk.client.SphereClient;

public abstract class AbstractService {
    protected final SphereClient client;

    protected AbstractService(final SphereClient client) {
        this.client = client;
    }
}
