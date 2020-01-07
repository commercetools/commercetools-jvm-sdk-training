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
                client.execute(
                        CartCreateCommand.of(
                                CartDraftBuilder.of(DefaultCurrencyUnits.EUR)
                                        .deleteDaysAfterLastModification(90)
                                        .customerEmail(customer.getEmail())
                                        .customerId(customer.getId())
                                        .country(customer.getDefaultShippingAddress().getCountry())
                                        .shippingAddress(customer.getDefaultShippingAddress())
                                        .inventoryMode(InventoryMode.RESERVE_ON_ORDER)
                                        .build()

                        )
                );
    }

    public CompletionStage<Cart> createAnonymousCart() {

        return
                client.execute(
                        CartCreateCommand.of(
                                CartDraftBuilder.of(DefaultCurrencyUnits.EUR)
                                        .deleteDaysAfterLastModification(90)
                                        .anonymousId("123456789")
                                        .country(CountryCode.DE)
                                        .build()
                        )
                );
    }


    public CompletionStage<Cart> addProductToCartBySkusAndChannel(final Cart cart, final Channel channel, final String ... skus) {

        final List<AddLineItem> lineItemsToAdd = Stream.of(skus)
                .map(s -> AddLineItem.of(
                            LineItemDraftBuilder.ofSku(s, (long) 1)
                                .supplyChannel(channel)
                                .build()
                        )
                )
                .collect(Collectors.toList());

        return
                client.execute(
                        CartUpdateCommand.of(cart, lineItemsToAdd)
                );
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
                client.execute(
                        CartUpdateCommand.of(cart,
                                AddDiscountCode.of(code))
                );

    }


    public CompletionStage<Cart> recalculate(final Cart cart) {

        return
                client.execute(
                        CartUpdateCommand.of(cart, Recalculate.of())
                );
    }


    public CompletionStage<Cart> setShipping(final Cart cart) {

        return
            client.execute(ShippingMethodsByCartGet.of(cart))
                .thenComposeAsync(shippingMethods -> client.execute(
                        CartUpdateCommand.of(cart, SetShippingMethod.ofId(shippingMethods.get(0).getId()))
                ));
    }


}
