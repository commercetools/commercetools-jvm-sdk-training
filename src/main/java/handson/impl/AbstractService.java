package handson.impl;

import io.sphere.sdk.client.SphereClient;

/**
 * Abstract base class for services.
 */
public abstract class AbstractService {
    protected final SphereClient client;

    protected AbstractService(final SphereClient client) {
        this.client = client;
    }
}
