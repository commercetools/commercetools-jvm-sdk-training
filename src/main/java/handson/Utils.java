package handson;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientConfig;

import java.io.IOException;
import java.util.Properties;

class Utils {

    /**
     * Creates a blocking sphere client
     * @return Sphere client
     * @throws IOException
     */
    static BlockingSphereClient createSphereClient() throws IOException {

        final SphereClientConfig clientConfig = loadCTPClientConfig();

        //TODO 1.1.3. Create the client
        return null;
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

        // For the US, uncomment following lines
        // final String authUrl = prop.getProperty("authUrl");
        // final String apiUrl = prop.getProperty("apiUrl");

        //TODO 1.1.2. Create the configuration for the sphere client
        return null;
    }

}