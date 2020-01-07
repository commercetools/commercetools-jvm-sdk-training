package handson.impl;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.*;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.*;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.models.Referenceable;
import io.sphere.sdk.models.ResourceIdentifier;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.shippingmethods.ShippingMethod;
import io.sphere.sdk.shippingmethods.queries.ShippingMethodsByCartGet;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides operations to work with {@link Cart}s.
 */
public class CartService extends AbstractService {

    public CartService(SphereClient client) {
        super(client);
    }

    /**
     * Creates a cart for the given customer.
     *
     * @param customer the customer
     * @return the customer creation completion stage
     */
    public CompletionStage<Cart> createCart(final Customer customer) {

        return
                null;
    }

    public CompletionStage<Cart> createAnonymousCart() {

        return
                null;
    }


    public CompletionStage<Cart> addProductToCartBySkusAndChannel(final Cart cart, final Channel channel, final String ... skus) {

        final List<AddLineItem> lineItemsToAdd;

        return
                null;

    }


    /**
     * Adds the given discount code to the given cart.
     *
     * @param code the discount code
     * @param cart the cart
     * @return the cart update completion stage
     */
    public CompletionStage<Cart> addDiscountToCart(final Cart cart, final String code) {

        return
                null;

    }


    public CompletionStage<Cart> recalculate(final Cart cart) {

        return
                null;
    }


    public CompletionStage<Cart> setShipping(final Cart cart) {

        return
            null;
    }


}
