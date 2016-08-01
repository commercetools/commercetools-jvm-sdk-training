package handson;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.products.queries.ProductProjectionQuery;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        try(final SphereClient client = createSphereClient()) {
            System.err.println(client.execute(ProductProjectionQuery.ofCurrent()).toCompletableFuture().join());
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
