package handson;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

import java.time.Duration;

class Utils {

    /**
     * Creates a blocking sphere client
     * @return Sphere client
     */
    static BlockingSphereClient createSphereClient() {
        final String projectKey = "fyayc-hou-42";
        final String clientId = "Olmu4RPsFJZvN82npylM6jII";
        final String clientSecret = "VdELOheKjPbprfWrbG5wrYUZZ01ojARr";

        final SphereClientConfig clientConfig = SphereClientConfig.of(projectKey, clientId, clientSecret);

        return BlockingSphereClient.of(SphereClientFactory.of().createClient(clientConfig), Duration.ofMinutes(1));
    }

}