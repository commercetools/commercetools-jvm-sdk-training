package handson;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static handson.Commands.*;

public class Project {

    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);

    static Product product;
    static Cart cart;
    static TaxCategory taxCategory;

    private static final String PRODUCT_NAME = "hiPhone";
    private static final String PRODUCT_KEY = "hiPhone-" + RandomStringUtils.randomAlphanumeric(10);
    private static final String PRODUCT_SKU = "hiPhone-7-gold-" + RandomStringUtils.randomAlphanumeric(10);
    private static final Long productVersion = 1L;

    /**
     * Sets up a project:
     * - queries a product type, or creates one if none is available
     * - creates a product
     * - queries a tax category, or creates one if none is available
     * - sets tax category to product
     * @param client CTP client
     */
    public static void setUpProject(final BlockingSphereClient client){
        //TODO 3.4. Call the methods queryFirstProductType and createProduct
        //System.out.println("Product type " + productType.getName() + " is queried/created.");
        //System.out.println("Product with id " + product.getId() + " is created.");

        //TODO 3.5.2. Call queryFirstTaxCategory
        //System.out.println("Tax category " + taxCategory.getName() + " is selected.");

        //TODO 3.5.4. Call setTaxCategoryWithProductKeyAndVersion
        //System.out.println("Tax category " + taxCategory.getName() + " is set to " + product.getId());
    }

    /**
     * Cleans up a project by deleting all products, all orders, etc.
     * @param client CTP client
     */
    public static void cleanUpProject(BlockingSphereClient client){
        List<Product> allProducts = queryAllProducts(client).getResults();
        System.out.println("All products are queried.");
        deleteProducts(client, allProducts);

        // deleteProductType(client, productType);

        List<TaxCategory> allTaxCategories = queryAllTaxCategories(client).getResults();
        deleteTaxCategories(client, allTaxCategories);

        List<Cart> cartList = queryAllCarts(client).getResults();
        deleteCarts(client, cartList);

        List<Order> allOrders = queryAllOrders(client).getResults();
        deleteAllOrders(client, allOrders);
    }

    /**
     * Deletes created product, tax category, cart, and order
     * @param client CTP Client
     * @param product The product to delete
     * @param taxCategory The tax category to delete
     * @param cart The cart to delete
     * @param order The order to delete
     */
    public static void cleanUpProject(BlockingSphereClient client,
                                      Product product,
                                      TaxCategory taxCategory,
                                      Cart cart,
                                      Order order) {
        deleteProduct(client, product);
        LOGGER.debug("Product with the id {} is deleted", product.getId());

        deleteTaxCategory(client, taxCategory);
        LOGGER.debug("Tax category with the id {} is deleted", taxCategory.getId());

        cart = queryCartById(client, cart.getId());
        deleteCart(client, cart);
        LOGGER.debug("Cart with the id {} is deleted", cart.getId());

        deleteOrder(client, order);
        LOGGER.debug("Order with the id {} is deleted", order.getId());
    }
}
