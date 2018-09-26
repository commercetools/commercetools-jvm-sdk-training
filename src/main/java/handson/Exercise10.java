package handson;

import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.Image;
import io.sphere.sdk.products.ImageDimensions;
import io.sphere.sdk.products.PriceDraftBuilder;
import io.sphere.sdk.products.PriceDraftDsl;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductDraftDsl;
import io.sphere.sdk.products.ProductVariantDraftBuilder;
import io.sphere.sdk.products.ProductVariantDraftDsl;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.utils.HighPrecisionMoneyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static handson.impl.ClientService.createSphereClient;
import static io.sphere.sdk.models.LocalizedString.ofEnglish;

//TODO 10.1 Please replace "yourName" in products.csv with your name.
public class Exercise10 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise10.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String inputFilePath = "/products.csv";
        final List<ProductDraft> productDrafts = processInputFile(inputFilePath);

        LOG.info("Parsed {} {}", inputFilePath, productDrafts);

        LOG.info("Starting Sync..");
        try (final SphereClient client = createSphereClient()) {

            //TODO 10.3 Sync the product drafts

            final ProductSyncOptions productSyncOptions = ProductSyncOptionsBuilder.of(client)
                                                                                   .errorCallback(LOG::error)
                                                                                   .warningCallback(LOG::warn)
                                                                                   .build();

            final ProductSync productSync = new ProductSync(productSyncOptions);
            productSync.sync(productDrafts)
                       .thenAcceptAsync(productSyncStatistics -> LOG.info(productSyncStatistics.getReportMessage()))
                       .toCompletableFuture()
                       .get();
        }
    }

    private static List<ProductDraft> processInputFile(@Nonnull final String inputFilePath) {
        List<ProductDraft> inputList = new ArrayList<>();
        try(final BufferedReader br = new BufferedReader(new InputStreamReader(Exercise10.class.getResourceAsStream(inputFilePath)))) {
            // skip the header of the csv
            inputList = br.lines()
                          .skip(1)
                          .map(Exercise10::processLine)
                          .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("error streaming file", e);
        }
        return inputList;
    }


    private static ProductDraftDsl processLine(@Nonnull final String line) {
        final String[] splitLine = line.split(",");
        final String productTypeKey = splitLine[0];
        final String productKey = splitLine[1];
        final String sku = splitLine[2];
        final String variantKey = splitLine[3];
        final String productName = splitLine[4];
        final String productDescription = splitLine[5];
        final double basePrice = Double.parseDouble(splitLine[6]);
        final String currencyCode = splitLine[7];
        final String imageUrl = splitLine[8];


        final PriceDraftDsl priceDraftDsl = PriceDraftBuilder
            .of(HighPrecisionMoneyImpl.of(BigDecimal.valueOf(basePrice), currencyCode, 3))
            .build();

        final Image image = Image.of(imageUrl, ImageDimensions.of(100, 100));

        //TODO 10.1 Create a ProductVariantDraft
        final ProductVariantDraftDsl variantDraftDsl = ProductVariantDraftBuilder.of()
                                                                                 .sku(sku)
                                                                                 .key(variantKey)
                                                                                 .prices(priceDraftDsl)
                                                                                 .images(image)
                                                                                 .build();

        //TODO 10.2 Create a ProductDraft and return it.
        return ProductDraftBuilder
            .of(ProductType.referenceOfId(productTypeKey), ofEnglish(productName), ofEnglish(sku),
                variantDraftDsl)
            .key(productKey)
            .description(ofEnglish(productDescription))
            .build();
    }
}
