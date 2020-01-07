package handson.impl;

import io.sphere.sdk.client.SphereAccessTokenSupplier;
import io.sphere.sdk.client.SphereAsyncHttpClientFactory;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.http.HttpClient;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Properties;

public class BaseTest {
    private SphereClient client;

    @Before
    public void setup() throws IOException {
        client = createSphereClient("/dev.properties");
    }

    protected SphereClient createSphereClient(String configLocation) throws IOException {
        final Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(configLocation));

        final SphereClientConfig sphereClientConfig = SphereClientConfig.ofProperties(properties, "");
        final HttpClient httpClient = SphereAsyncHttpClientFactory.create();
        final SphereAccessTokenSupplier tokenSupplier = SphereAccessTokenSupplier.ofAutoRefresh(sphereClientConfig, httpClient, true);
        return SphereClient.of(sphereClientConfig, httpClient, tokenSupplier);
    }

    @After
    public void teardown() {
        client.close();
    }

    protected SphereClient client() {
        return client;
    }
}
