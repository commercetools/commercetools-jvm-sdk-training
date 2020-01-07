package handson.impl;

import io.sphere.sdk.client.SphereAccessTokenSupplier;
import io.sphere.sdk.client.SphereAsyncHttpClientFactory;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.http.HttpClient;

import java.io.IOException;
import java.util.Properties;

public class ClientService {
    /**
     * Creates a blocking sphere client
     * @return Sphere client
     * @throws IOException
     */
    public static SphereClient createSphereClient() throws IOException {

        final SphereClientConfig clientConfig = loadCTPClientConfig();

        return null;
    }

    /**
     * Sets a sphere client configuration
     * @return sphere client configuration
     * @throws IOException
     */
    private static SphereClientConfig loadCTPClientConfig() throws IOException {

        return null;


    }
}