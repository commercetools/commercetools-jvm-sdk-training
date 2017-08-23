package handson;

import io.sphere.sdk.client.*;
import io.sphere.sdk.http.HttpClient;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Properties;

public class BaseTest {
    private SphereClient client;

    @Before
    public void setup() throws IOException {
        final Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/dev.properties"));

        final SphereClientConfig sphereClientConfig = SphereClientConfig.ofProperties(properties, "");
        final HttpClient httpClient = SphereAsyncHttpClientFactory.create();
        SphereAccessTokenSupplier tokenSupplier = SphereAccessTokenSupplier.ofAutoRefresh(sphereClientConfig, httpClient, true);
        client = SphereClient.of(sphereClientConfig, httpClient, tokenSupplier);
    }

    @After
    public void teardown() {
        client.close();
    }

    protected SphereClient client() {
        return client;
    }
}
