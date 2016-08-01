package handson;

import io.sphere.sdk.carts.CartLike;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import static handson.ProjectInitialization.FRAUD_SCORE_FIELD;

public class Utils {

    public static void printCart(final CartLike<?> cartLike, final String message) {
        System.err.println("");
        System.err.println(message + ":");
        final int quantity = cartLike.getLineItems().size();
        System.err.println("Line items: " + quantity);
        cartLike.getLineItems().forEach(lineItem -> System.err.println("  " + lineItem));
        if (cartLike.getCustom() != null) {
            Optional.ofNullable(cartLike.getCustom().getFieldAsLong(FRAUD_SCORE_FIELD))
                    .ifPresent(fraudScore -> System.err.println("Fraud score: " + fraudScore));
        } else {
            System.err.println("Fraud score: <Unavailable>");
        }
    }

    public static BlockingSphereClient createSphereClient() throws IOException {
        final SphereClientConfig clientConfig = loadCommercetoolsPlatformClientConfig();
        return BlockingSphereClient.of(SphereClientFactory.of().createClient(clientConfig), Duration.ofMinutes(1));
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
