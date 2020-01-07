package handson;

import com.commercetools.importer.models.importsinks.ImportSink;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sphere.sdk.client.HttpRequestIntent;
import io.sphere.sdk.client.JsonNodeSphereRequest;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.CreateCommand;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


public class Moodle56_IMPORT {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle56_IMPORT.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        try (final SphereClient client = createSphereClient()) {

            // todo
            //
            // Finalize code to retrieve status
            // Why are there no CreateCommands?
            // How to use a product draft and then the update actions are calculated
            // ??

            // TODO Import API Separated Products, Variants, Prices
            // WORKS but bad solution
        String importSinkDraft = "{" +
                        "\"key\": \"berlin-store-prices\"," +
                        "\"resourceType\": \"category\"" +
                "}";

        JsonNode jsonNodeBody = new ObjectMapper().readTree(importSinkDraft);
        LOG.info("JSON answer is {} ",
                client.execute(JsonNodeSphereRequest.of(HttpMethod.POST,
                        "/import-sinks",
                        jsonNodeBody)
                )
                .toCompletableFuture().get()
        );

        // ImportSinkDraftBuilder available but no ImportSinkCreateCommand
            //ImportSinkDraft importSinkDraft = ImportSinkDraftBuilder.of()
            //        .key("berlin-warehouse-price-import")
            //        .resourceType(ImportResourceType.PRICE)
            //        .build();
            //CreateCommand<ImportSink> importSinkCreateCommand ???




            // Step 2: Import prices
            // works but bad solution
            //
            String importRequest = "{" +
                    "\"type\": \"price\"," +
                    "\"resources\": [{" +

                    "\"key\": \"berlin-store-prices\"," +
                    "\"value\": {  \"currencyCode\": \"EUR\", \"centAmount\": \"4200\" }," +
                    "\"productVariant\": {  \"typeId\": \"product-variant\", \"key\": \"123\" }," +
                    "\"product\": {  \"typeId\": \"product\", \"key\": \"123\" }" +
                    "} ] }";

            jsonNodeBody = new ObjectMapper().readTree(importRequest);
            LOG.info("JSON answer is {} ",
                    client.execute(JsonNodeSphereRequest.of(HttpMethod.POST,
                            "/prices/importSinkKey=" + "berlin-store-prices",
                            jsonNodeBody)
                    )
                            .toCompletableFuture().get()
            );


            // Check the status of the operation
            // Does NOT work for single import operation status, only whole summary
            //

            LOG.info("JSON answer is {} ",
                    client.execute(JsonNodeSphereRequest.of(HttpMethod.GET,
                            "/import-summaries/importSinkKey=berlin-store-prices",
                            null)
                    )
                            .toCompletableFuture().get()
            );




            // TODO Import API Using a draft
            //
            //


        }
    }
}

// Created Sink
// {"key":"berlin-store-prices","resourceType":"price","version":0,"createdAt":"2019-12-31T14:10:39.278Z","lastModifiedAt":"2019-12-31T14:10:39.278Z"}

// Created Request
// 15:44:40.770 [main] INFO  handson.ExerciseMoodle56_IMPORT - JSON answer is {"operationStatus":[{"operationId":"55744f53-81d6-404d-9ec4-1544cf8bcced","state":"Accepted","errors":[]}]}

// Summary Report
// 16:06:41.017 [main] INFO  handson.Moodle56_IMPORT - JSON answer is {"states":{"WaitForMasterVariant":0,"Resolved":1,"Skipped":0,"Accepted":0,"Unresolved":0,"Deleted":0,"Imported":0,"Rejected":0,"Expired":0,"ValidationFailed":0},"total":1}