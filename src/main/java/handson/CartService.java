package handson;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraftBuilder;
import io.sphere.sdk.carts.CartDraftDsl;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;

import java.util.concurrent.CompletionStage;

public class CartService extends AbstractService {

    public CartService(SphereClient client) {
        super(client);
    }

    public CompletionStage<Cart> createCart(final Customer customer) {
        final CartDraftDsl cartDraft = CartDraftBuilder.of(DefaultCurrencyUnits.EUR)
                .customerId(customer.getId())
                .deleteDaysAfterLastModification(1)
                .build();
        return client.execute(CartCreateCommand.of(cartDraft));
    }

    public CompletionStage<Cart> addToCart(final Cart cart, final ProductProjection productProjection) {
        final AddLineItem addLineItem = AddLineItem.of(productProjection.getId(), productProjection.getMasterVariant().getId(), 1L);
        return client.execute(CartUpdateCommand.of(cart, addLineItem));
    }
}
