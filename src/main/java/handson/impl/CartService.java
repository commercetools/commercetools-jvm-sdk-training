package handson.impl;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraftBuilder;
import io.sphere.sdk.carts.CartDraftDsl;
import io.sphere.sdk.carts.LineItemDraftBuilder;
import io.sphere.sdk.carts.LineItemDraftDsl;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddDiscountCode;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ByIdVariantIdentifier;
import io.sphere.sdk.products.ProductProjection;

import java.util.concurrent.CompletionStage;

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
        final CartDraftDsl cartDraft = CartDraftBuilder.of(DefaultCurrencyUnits.EUR)
                                                       .country(CountryCode.DE)
                                                       .shippingAddress(Address.of(CountryCode.DE))
                                                       .customerId(customer.getId())
                                                       .deleteDaysAfterLastModification(1)
                                                       .build();
        return client.execute(CartCreateCommand.of(cartDraft));
    }

    /**
     * Adds the given product to the given cart.
     *
     * @param product the product
     * @param cart    the cart
     * @return the cart update completion stage
     */
    public CompletionStage<Cart> addProductToCart(final ProductProjection product, final Cart cart) {
        final LineItemDraftDsl lineItemDraft = LineItemDraftBuilder
            .ofVariantIdentifier(ByIdVariantIdentifier.of(product, 1), 1L)
            .build();

        final AddLineItem addLineItem = AddLineItem.of(lineItemDraft);

        return client.execute(CartUpdateCommand.of(cart, addLineItem));
    }

    /**
     * Adds the given discount code to the given cart.
     *
     * @param code the discount code
     * @param cart the cart
     * @return the cart update completion stage
     */
    public CompletionStage<Cart> addDiscountToCart(final String code, final Cart cart) {
        return client.execute(CartUpdateCommand.of(cart, AddDiscountCode.of(code)));
    }
}
