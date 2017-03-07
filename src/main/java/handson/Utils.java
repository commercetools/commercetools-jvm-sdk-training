package handson;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

class Utils {

    /**
     * Creates a blocking sphere client
     * @return Sphere client
     */
    static BlockingSphereClient createSphereClient() throws IOException {

        final SphereClientConfig clientConfig = loadCTPClientConfig();
        return BlockingSphereClient.of(SphereClientFactory.of().createClient(clientConfig), Duration.ofMinutes(1));
    }

    /**
     * Sets a sphere client configuration
     * @return sphere client configuration
     * @throws IOException
     */
    private static SphereClientConfig loadCTPClientConfig() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader()
                        .getResourceAsStream("dev.properties"));

        final String projectKey = prop.getProperty("projectKey");
        final String clientId = prop.getProperty("clientId");
        final String clientSecret = prop.getProperty("clientSecret");

        return SphereClientConfig.of(projectKey, clientId, clientSecret);
    }
}