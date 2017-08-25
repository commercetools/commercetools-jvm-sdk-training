package handson.impl;

import io.sphere.sdk.client.SphereAccessTokenSupplier;
import io.sphere.sdk.client.SphereAsyncHttpClientFactory;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.http.HttpClient;

import java.io.IOException;
import java.util.Properties;

public class Utils {
    /**
     * Creates a blocking sphere client
     * @return Sphere client
     * @throws IOException
     */
    public static SphereClient createSphereClient() throws IOException {
        final SphereClientConfig clientConfig = loadCTPClientConfig();

        //TODO 1.1.3. Create the client
        final HttpClient httpClient = SphereAsyncHttpClientFactory.create();
        final SphereAccessTokenSupplier tokenSupplier = SphereAccessTokenSupplier.ofAutoRefresh(clientConfig, httpClient, true);
        return SphereClient.of(clientConfig, httpClient, tokenSupplier);
    }

    /**
     * Sets a sphere client configuration
     * @return sphere client configuration
     * @throws IOException
     */
    private static SphereClientConfig loadCTPClientConfig() throws IOException {
        final Properties prop = new Properties();
        prop.load(Utils.class.getResourceAsStream("/dev.properties"));

        //TODO 1.1.2. Create the configuration for the sphere client
        return SphereClientConfig.ofProperties(prop, "");
    }
}