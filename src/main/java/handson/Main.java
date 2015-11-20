package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductQuery;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        final SphereClientConfig clientConfig = SphereClientConfig.of(prop.getProperty("projectKey"), prop.getProperty("clientId"), prop.getProperty("clientSecret"));
        try(final SphereClient client = SphereClientFactory.of().createClient(clientConfig)) {
            System.err.println(client.execute(ProductProjectionQuery.ofCurrent()).toCompletableFuture().join());
        }
    }
}
