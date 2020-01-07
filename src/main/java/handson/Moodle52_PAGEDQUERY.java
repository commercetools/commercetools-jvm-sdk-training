package handson;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.queries.PagedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


public class Moodle52_PAGEDQUERY {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle52_PAGEDQUERY.class);




    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            // UseCases
            // Fetching ALL products
            // Fetching ALL products of a certain type
            // Fetching ALL orders
            // Pagination of some entities BUT only ordered via id

            // Pagination is down to max 10.000
            final int PAGE_SIZE = 2;

            // Instead of asking for next page, ask for elements being greater than this id

            // TODO in class:
            // Give last id, start with slightly modified first id OR: do not use id when fetching first page
            // Give product type id
            //
            String last_id = "460c9d6e-3eee-4525-8c53-7bede9d7a4aa";
            String productTypeId = "7f30329c-bfaa-4c75-97bf-58caf1103900";

            final ProductQuery seedQueryManualPaging = ProductQuery.of()
                    // withPredicate or without Prodicate for filtering
                    .withPredicates(m -> m.productType().is(ProductType.reference(productTypeId)))

                    // Important, internally we use id > $lastId, it will not work without this line
                    .withSort(m -> m.id().sort().asc())

                    // Limit the size per page
                    .withLimit(PAGE_SIZE)

                    // use this for following pages
                    .plusPredicates(m -> m.id().isGreaterThan(last_id))

                    // always use this
                    .withFetchTotal(false);

            final PagedQueryResult<Product> productPagedQueryResult = client.execute(seedQueryManualPaging)
                            .toCompletableFuture().get();

            // Print results
            LOG.info("Found product size: {}", productPagedQueryResult.size());
            for (Product product : productPagedQueryResult.getResults()) {
                LOG.info("Product Id: {}", product.getId());
            }

        }
    }
}
