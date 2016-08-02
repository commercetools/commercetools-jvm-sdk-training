package handson;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        try(final BlockingSphereClient client = createSphereClient()) {
            final ProductProjectionQuery sphereRequest = ProductProjectionQuery.ofCurrent();
            final PagedQueryResult<ProductProjection> queryResult = client.executeBlocking(sphereRequest);
            System.err.println(queryResult);
        }
    }

    private static BlockingSphereClient createSphereClient() throws IOException {
        final SphereClientConfig clientConfig = loadCommercetoolsPlatformClientConfig();
        final SphereClient client = SphereClientFactory.of().createClient(clientConfig);
        return BlockingSphereClient.of(client, Duration.ofMinutes(1));
    }

    private static SphereClientConfig loadCommercetoolsPlatformClientConfig() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        final String projectKey = prop.getProperty("projectKey");
        final String clientId = prop.getProperty("clientId");
        final String clientSecret = prop.getProperty("clientSecret");
        return SphereClientConfig.of(projectKey, clientId, clientSecret);
    }
}
