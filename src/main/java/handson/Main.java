package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.ChangeLineItemQuantity;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.models.Versioned;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class Main {
    private static void doSomething(final SphereClient client) {
        final Cart cart = createCart(client);
        final String sku = "book-sku";

        final PagedQueryResult<ProductProjection> queryResult = execute(client, ProductProjectionQuery.ofCurrent()
                .withPredicates(product -> product.allVariants().where(variant -> variant.sku().is(sku))));

        if (!queryResult.getResults().isEmpty()) {
            final ProductProjection productProjection = queryResult.getResults().get(0);
            final String productId = productProjection.getId();
            final Optional<ProductVariant> variantOptional = productProjection.findVariantBySky(sku);
            if (variantOptional.isPresent()) {

                final ProductVariant productVariant = variantOptional.get();
                final int quantity = 1;

                final Cart updatedCart = addToCart(client, cart, productId, productVariant, quantity);

                final Cart cartWithMore = changeLineItemQuantity(client, updatedCart);

                final String sapOrderNumber = "012345";

            }
        }
    }

    private static Cart createCart(final SphereClient client) {
        final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR)
                .withCountry(CountryCode.DE);
        final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
        return execute(client, cartCreateCommand);
    }

    private static Cart changeLineItemQuantity(final SphereClient client, final Cart cart) {
        final ChangeLineItemQuantity changeLineItemQuantity = ChangeLineItemQuantity.of(cart.getLineItems().get(0).getId(), 4);
        return execute(client, CartUpdateCommand.of(cart, changeLineItemQuantity));
    }

    private static Cart addToCart(final SphereClient client, final Cart cart, final String productId, final ProductVariant productVariant, final int quantity) {
        final AddLineItem addLineItem = AddLineItem.of(productId, productVariant.getId(), quantity);
        return execute(client, CartUpdateCommand.of(cart, addLineItem));
    }

    private static <T> T execute(final SphereClient client, final SphereRequest<T> sphereRequest) {
        return client.execute(sphereRequest).toCompletableFuture().join();
    }


    public static void main(String[] args) throws IOException {
        final Properties prop = loadCommercetoolsPlatformProperties();
        final String projectKey = prop.getProperty("projectKey");
        final String clientId = prop.getProperty("clientId");
        final String clientSecret = prop.getProperty("clientSecret");
        final SphereClientConfig clientConfig = SphereClientConfig.of(projectKey, clientId, clientSecret);
        try(final SphereClient client = SphereClientFactory.of().createClient(clientConfig)) {
            doSomething(client);
        }
    }



    private static Properties loadCommercetoolsPlatformProperties() throws IOException {
        final Properties prop = new Properties();
        prop.load(Main.class.getClassLoader().getResourceAsStream("dev.properties"));
        return prop;
    }
}
