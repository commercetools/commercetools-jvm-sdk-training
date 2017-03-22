package handson;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.CartDraftBuilder;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.commands.OrderDeleteCommand;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.queries.OrderQuery;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.attributes.AttributeDefinitionBuilder;
import io.sphere.sdk.products.attributes.StringAttributeType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.commands.updateactions.SetTaxCategory;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductProjectionQueryModel;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.products.search.ProductDataFacetSearchModel;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.products.search.ProductProjectionSearchModel;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.QueryPredicate;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.search.TermFacetExpression;
import io.sphere.sdk.search.TermStats;
import io.sphere.sdk.taxcategories.*;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.taxcategories.commands.TaxCategoryDeleteCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.sphere.sdk.models.DefaultCurrencyUnits.EUR;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;

public class Commands {

    private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

    private static final String PRODUCT_TYPE_KEY = RandomStringUtils.randomAlphanumeric(10);
    private static  final String PRODUCT_TYPE_NAME = "Mobile Phone";

    public static final ProductDataFacetSearchModel PRODUCT_MODEL = ProductProjectionSearchModel.of().facet();

    /**
     * Creates a Product Type with attribute:
     * - "color", of type String
     * @param client CTP client
     * @return the created product type
     */
    public static ProductType createProductType(final BlockingSphereClient client) {
        final ProductTypeDraft draft = ProductTypeDraft.of(PRODUCT_TYPE_KEY, PRODUCT_TYPE_NAME, "", singletonList(
                AttributeDefinitionBuilder.of("color", LocalizedString.of(ENGLISH, "Color"), StringAttributeType.of()).build()));
        final ProductTypeCreateCommand createCommand = ProductTypeCreateCommand.of(draft);

        return client.executeBlocking(createCommand);
    }

    /**
     * Queries the first product type, if none available it creates one
     * @param client CTP client
     * @return Queried or created prodyct type
     */
    public static ProductType queryFirstProductType(final BlockingSphereClient client) {
        ProductTypeQuery request = ProductTypeQuery.of();
        return client.executeBlocking(request)
                .head()
                .orElseGet(() -> createProductType(client));
    }

    /**
     * Delete a given product type
     * @param client CTP client
     * @param productType The product type to delete
     */
    public static void deleteProductType(BlockingSphereClient client, ProductType productType){
        final ProductTypeDeleteCommand deleteCommand = ProductTypeDeleteCommand.of(productType);
        client.executeBlocking(deleteCommand);
    }

    /**
     * Creates a Product with the given name and random slug and price in EUR and key.
     * @param client CTP client
     * @param productType the Product Type defining the allowed attributes of the product
     * @param name the name of the product
     * @param key the key of the product
     * @param sku the sku of the master variant
     * @return the created product
     */
    public static Product createProduct(final BlockingSphereClient client, final ProductType productType,
                                        final String name, final String key, final String sku) {
        final LocalizedString localizedName = LocalizedString.of(ENGLISH, name);
        final LocalizedString randomSlug = LocalizedString.of(ENGLISH, RandomStringUtils.randomAlphanumeric(10));

        final ProductVariantDraft masterVariant = ProductVariantDraftBuilder.of()
                .price(PriceDraft.of(BigDecimal.valueOf(RandomUtils.nextLong(300, 1000)), EUR))
                .sku(sku)
                .build();

        final ProductDraft draft = ProductDraftBuilder.of(productType, localizedName, randomSlug, masterVariant)
                .key(key)
                .build();

        final ProductCreateCommand createCommand = ProductCreateCommand.of(draft);

        return client.executeBlocking(createCommand);
    }

    /**
     * Queries a Product by id
     * @param client CTP client
     * @param sku the sku of the product
     * @return the requested product
     */
    public static ProductVariant queryProductVariant(final BlockingSphereClient client, final String sku)
            throws ExecutionException, InterruptedException {
        QueryPredicate<ProductProjection> hasSku = ProductProjectionQueryModel.of()
                .allVariants().where(variantsModel -> variantsModel.sku().is(sku));
        ProductProjectionQuery request = ProductProjectionQuery.ofStaged().withPredicates(hasSku);

        return client.executeBlocking(request)
                .head()
                .flatMap(product -> product.findVariantBySku(sku))
                .orElseThrow(() -> new IllegalArgumentException("Product does not exist"));
    }

    /**
     * Publish a given product
     * @param client CTP client
     * @param key the id of the product to update
     * @return the updated product (with the updates)
     */
    public static Product publishProduct(final BlockingSphereClient client, final String key, final Long version){
        final ProductUpdateCommand updateCommand = ProductUpdateCommand.ofKey(key, version, Publish.of());
        return client.executeBlocking(updateCommand);
    }

    /**
     * Unpublishes a given  product
     * @param client CTP client
     * @param key The key of the product to update
     * @param version The current version of the product to publish
     * @return The product after unpublishing
     */
    public static Product unPublishProduct(final BlockingSphereClient client, final String key, final Long version){
        final ProductUpdateCommand updateCommand = ProductUpdateCommand.ofKey(key, version, Publish.of());
        return client.executeBlocking(updateCommand);
    }

    /**
     * Set a tax category to a product
     * @param client CTP client
     * @param key The key of the product to update
     * @param version The current version of the product to update
     * @param taxCategory The tax category to set
     * @return The product after setting tax category
     */
    public static Product setTaxCategoryWithProductKeyAndVersion(final BlockingSphereClient client,
                                                                 final String key,
                                                                 final Long version,
                                                                 final TaxCategory taxCategory){
        final ProductUpdateCommand updateCommand = ProductUpdateCommand.ofKey(key, version, SetTaxCategory.of(taxCategory));
        return client.executeBlocking(updateCommand);
    }

    /**
     * Deletes the given Product.
     * @param client CTP client
     * @param product Product to delete
     */
    public static void deleteProduct(final BlockingSphereClient client, final Product product) {
        final ProductDeleteCommand deleteCommand = ProductDeleteCommand.of(product);
        client.executeBlocking(deleteCommand);
        System.out.println("[DELETE] Product with id " + product.getId() + " is deleted.");
    }

    /**
     * Creates a tax category
     * @param client CTP client
     * @param name name of the category to create
     * @return Created tax category
     */
    public static TaxCategory createTaxCategory(final BlockingSphereClient client, final String name, final Double amount){

        TaxRateDraft taxRateDraft = TaxRateDraftBuilder.of("tax rate", amount, true, CountryCode.DE).build();
        List<TaxRateDraft> taxRateDraftList = singletonList(taxRateDraft);
        TaxCategoryDraft taxCategoryDraft = TaxCategoryDraftBuilder.of(name, taxRateDraftList, "My default tax category.").build();

        final TaxCategoryCreateCommand command = TaxCategoryCreateCommand.of(taxCategoryDraft);
        return client.executeBlocking(command);
    }

    /**
     * Queries first tax category, or creates one if none is available
     * @param client CTP client
     * @return the queried or created tax category
     */
    public static TaxCategory queryFirstTaxCategory(final BlockingSphereClient client){
        TaxCategoryQuery request = TaxCategoryQuery.of();
        return client.executeBlocking(request)
                .head()
                .orElseGet(() -> createTaxCategory(client, "Default Tax", 0.1));
    }

    /**
     * Deletes a given tax category
     * @param client CTP client
     * @param taxCategory The tax category to delete
     */
    public static void deleteTaxCategory(final BlockingSphereClient client, final TaxCategory taxCategory){
        TaxCategoryDeleteCommand deleteCommand = TaxCategoryDeleteCommand.of(taxCategory);
        client.executeBlocking(deleteCommand);
        System.out.println("[DELETE] Tax category with id " + taxCategory.getName() + " is deleted.");
    }

    /**
     * Queries all tax categories
     * @param client CTP client
     * @return Queried categories
     */
    public static PagedQueryResult<TaxCategory> queryAllTaxCategories(BlockingSphereClient client){
        TaxCategoryQuery request = TaxCategoryQuery.of();
        return client.executeBlocking(request);
    }

    /**
     * Deletes given tax categories
     * @param client CTP client
     * @param taxCategoryList List of the categories to delete
     */
    public static void deleteTaxCategories(final BlockingSphereClient client, final List<TaxCategory> taxCategoryList){
        taxCategoryList.forEach(taxCategory -> deleteTaxCategory(client, taxCategory));
    }

    /**
     * Creates a Cart with the given name
     * @param client CTP client
     * @param currencyCode Currency of the cart to create
     * @return the created cart
     */
    public static Cart createCart(final BlockingSphereClient client, final String currencyCode){

        final CurrencyUnit currencyUnit = CurrencyUnitBuilder.of(currencyCode, currencyCode).build();
        final CartDraft cartDraft = CartDraftBuilder.of(currencyUnit).build();

        final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
        return client.executeBlocking(cartCreateCommand);
    }

    /**
     * Queries a cart by its id
     * @param client CTP client
     * @param cartId Id of the cart to query
     * @return Queried cart
     */
    public static Cart queryCartById(final BlockingSphereClient client, final String cartId){
        final CartByIdGet cartQuery = CartByIdGet.of(cartId);
        return client.executeBlocking(cartQuery);
    }

    /**
     * Queries a cart, or creates one if none is available
     * @param client CTP client
     * @return Returns queried or created cart
     */
    public static Cart queryFirstCart(final BlockingSphereClient client){
        String currencyCode = "EUR";
        CartQuery request = CartQuery.of();
        return client.executeBlocking(request)
                .head()
                .orElseGet(() -> createCart(client, currencyCode)); // Lambda supplier
    }

    /**
     * Queries all carts
     * @param client CTP client
     * @return All queried carts
     */
    public static PagedQueryResult<Cart> queryAllCarts(BlockingSphereClient client){
        CartQuery request = CartQuery.of();
        return client.executeBlocking(request);
    }

    /**
     * Deletes give cart
     * @param client CTP client
     * @param cart The cart to delete
     */
    public static void deleteCart(final BlockingSphereClient client, Cart cart){
        CartDeleteCommand deleteCommand = CartDeleteCommand.of(cart);
        client.executeBlocking(deleteCommand);
        System.out.println("[DELETE] Cart with id " + cart.getId() + " is deleted.");
    }

    /**
     * Deletes a given list of carts
     * @param client CTP client
     * @param cartList List of the carts to delete
     */
    public static void deleteCarts(final BlockingSphereClient client, List<Cart> cartList){
        cartList.forEach(cart -> deleteCart(client, cart));
    }

    /**
     * Adds a product to a cart
     * @param client CTP client
     * @param productId product to add to the cart
     * @param cart the cart to add the product to
     * @return The cart after adding the product
     */
    public static Cart addProductToCart(final BlockingSphereClient client, final String productId, final Cart cart, final Long quantity){
        final int MASTER_VARIANT_ID = 1;
        final AddLineItem action = AddLineItem.of(productId, MASTER_VARIANT_ID, quantity);
        final CartUpdateCommand command = CartUpdateCommand.of(cart, action);

        return client.executeBlocking(command);
    }

    /**
     * Sets a shipping address to a cart
     * @param client CT client
     * @param address the address to be added
     * @param cart the cart to set the address to
     * @return The cart after adding the shipping address
     */
    public static Cart setShippingAddress(final BlockingSphereClient client, final Address address, final Cart cart){
        UpdateAction<Cart> action = SetShippingAddress.of(address);
        CartUpdateCommand command = CartUpdateCommand.of(cart, action);

        return client.executeBlocking(command);
    }

    /**
     * Creates an order from a cart
     * @param client CTP client
     * @param cart the cart to create an order from
     * @return the created order
     */
    public static Order createOrderFromCart(final BlockingSphereClient client, final Cart cart){
        final OrderFromCartCreateCommand command = OrderFromCartCreateCommand.of(cart);
        return client.executeBlocking(command);
    }

    /**
     * Deletes a given order
     * @param client CTP client
     * @param order The order to delete
     */
    public static void deleteOrder(final BlockingSphereClient client, Order order){
        OrderDeleteCommand deleteCommand = OrderDeleteCommand.of(order);
        client.executeBlocking(deleteCommand);
        System.out.println("[DELETE] Order with id " + order.getId() + " is deleted.");
    }

    /**
     * Queries all orders
     * @param client CTP client
     * @return Queried orders
     */
    public static PagedQueryResult<Order> queryAllOrders(final BlockingSphereClient client){
        OrderQuery request = OrderQuery.of();
        return client.executeBlocking(request);
    }

    /**
     * Deletes all orders
     * @param client CTP client
     * @param orderList List of orders to delete
     */
    public static void deleteAllOrders(final BlockingSphereClient client, List<Order> orderList){
        orderList.forEach(order -> deleteOrder(client, order));
    }

    /**
     * Queries all products
     * @param client CTP client
     * @return Queried products
     */
    public static PagedQueryResult<Product> queryAllProducts(BlockingSphereClient client){
        ProductQuery request = ProductQuery.of();
        return client.executeBlocking(request);
    }

    /**
     * Deletes given products
     * @param client CTP client
     * @param productList the list of the products to delete
     */
    public static void deleteProducts(BlockingSphereClient client, List<Product> productList){
        productList.forEach(product -> deleteProduct(client, product));
    }

    /**
     * Creates facets created using the given attribute
     * @param client CTP client
     * @param attributeName name of the attribute used to create the facets
     * @return list of terms with count
     */
    public static List<TermStats> termFacetsForAttributes(BlockingSphereClient client, String attributeName){
        final TermFacetExpression<ProductProjection> facetExpr = PRODUCT_MODEL.allVariants().attribute().ofString(attributeName).allTerms();
        final ProductProjectionSearch search = ProductProjectionSearch.ofStaged().plusFacets(facetExpr);

        return client.executeBlocking(search)
                .getFacetResult(facetExpr)
                .getTerms();
    }

    /**
     * Filters products using given attribute name and value
     * @param client CTP client
     * @param attributeName name of the attribute used for filtering e.g. "color"
     * @param attributeValue value of the attribute used for filtering e.g. "red"
     * @return matching products
     */
    public static PagedSearchResult<ProductProjection> filterByTerm(BlockingSphereClient client,
                                                                    String attributeName,
                                                                    String attributeValue) {

        ProductProjectionSearch search = ProductProjectionSearch.ofStaged()
                .plusQueryFilters(productModel -> productModel.allVariants()
                        .attribute().ofString(attributeName).is(attributeValue));

        return client.executeBlocking(search);
    }

    /**
     * Counts the products filtered by the given attribute name and value
     * @param client CTP client
     * @param attributeName name of the attribute used for filtering e.g. "color"
     * @param attributeValue value of the attribute used for filtering e.g. "red"
     * @return count of matching products
     */
    public static Long countProductsFilterByTerm(BlockingSphereClient client,
                                                 String attributeName,
                                                 String attributeValue){
        return filterByTerm(client, attributeName, attributeValue).getTotal();
    }

}