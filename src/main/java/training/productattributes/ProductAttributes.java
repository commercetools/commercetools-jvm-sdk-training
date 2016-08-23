package training.productattributes;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.sphere.sdk.client.SphereClientUtils.blockingWait;
import static io.sphere.sdk.queries.QueryExecutionUtils.queryAll;
import static java.util.Arrays.asList;

@SpringBootApplication
@ComponentScan("common")
public class ProductAttributes implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.exit(SpringApplication.run(ProductAttributes.class, args));
    }

    @Autowired
    private BlockingSphereClient client;

    @Override
    public void run(final String... strings) throws Exception {
        final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent()
                .withPredicates(m -> m.slug().lang(Locale.ENGLISH).is("gabs-bag-cyndi-large-brown"));
        final Optional<ProductProjection> head = client.executeBlocking(query).head();
        final ProductProjection productProjection = head.orElseThrow(() -> new InvalidStateException("product does not exist"));
        showSingleAttribute(productProjection);
        showTable(productProjection);
//        IO.toast(productProjection);
    }

    private void showTable(final ProductProjection productProjection) {
        final List<ProductType> productTypes = blockingWait(queryAll(client, ProductTypeQuery.of()), 2, TimeUnit.MINUTES);
        final ProductAttributeFormatter formatter = new ProductAttributeFormatter(productTypes, asList(Locale.ENGLISH, Locale.GERMAN));

        final List<ImmutablePair<String, String>> data = productProjection.getMasterVariant().getAttributes().stream()
                .sorted(Comparator.comparing(attribute -> attribute.getName()))//here we just sort by attribute name
                .map(attribute -> {
                    final String value = formatter.convert(attribute, productProjection.getProductType());
                    return ImmutablePair.of(attribute.getName(), value);
                })
                .collect(Collectors.toList());

        final int leftSize = data.stream().mapToInt(pair -> pair.getLeft().length()).max().orElse(1);
        final int rightSize = data.stream().mapToInt(pair -> pair.getRight().length()).max().orElse(1);

        final String format = "%-" + leftSize + "s" + "|" + "%-" + rightSize + "s" + "\n";

        data.forEach(pair -> {
            System.err.printf(format, pair.getLeft(), pair.getRight());
        });
    }

    private void showSingleAttribute(final ProductProjection productProjection) {
        productProjection.getAllVariants()
                .forEach(variant -> System.err.println(variant.getAttribute("color").getValueAsLocalizedEnumValue()));
    }
}
