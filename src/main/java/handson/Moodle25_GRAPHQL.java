package handson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.graphql.Products;
import handson.impl.ThirdPartyClientService;
import handson.json.TokenAnswer;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;
import io.sphere.sdk.client.JsonNodeSphereRequest;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.http.HttpMethod;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;



public class Moodle25_GRAPHQL {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle25_GRAPHQL.class);

    public void fetchProductTotalsViaGraphQLandNodes(String token, String projectID) {

       try {
           Map<String, String> headers = new HashMap<>();
           headers.put("Authorization", "Bearer " + token);
           // replace in Java 9 with .headers(Map.of("Authorization", "Bearer " + token))

           GraphQLResponseEntity<Products> responseEntity =
                   null;
           LOG.info("Total products: {}", "");

       }
       catch (Exception e) {
           LOG.info(e.toString());
       }
    }

    public void fetchProductTotalsViaGraphQLandSphereclient() {

        try (final SphereClient client = createSphereClient()) {

            String jsonString_WithVariables = "{\"query\": \"query ($sku: String!) { product(sku: $sku) {id version}}\", \"variables\": {\"sku\": \"SKU-123\"}}";
            String jsonString_SimpleRequest = "{\"query\": \"query { products { total } }\", \"variables\": { } }";


            final JsonNode jsonNodeBody = new ObjectMapper().readTree(jsonString_SimpleRequest);
            LOG.info("JSON answer is {} ",
                    ""
            );

        }
        catch (IOException e) {
            LOG.info(e.toString());
        }
    }



    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Moodle25_GRAPHQL moodle25_graphql = new Moodle25_GRAPHQL();

        // TODO: Perform a graphql call using JsonNodeSphereRequest
        //
        moodle25_graphql.fetchProductTotalsViaGraphQLandSphereclient();


        // TODO: Perform a graphql call using Nodes
        //
        String clientID = "5HEQ28SkOxxAad7q34oTA1G6";                   // TODO Parse from dev.properties
        String clientSecret = "y2dPYAB2HckRpMcPlxkir8Zdv_iLA1Ks";
        String projectID = "training-etribes-dec2019-def";

        ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();
        String token =thirdPartyClientService.createClientAndFetchToken(clientID, clientSecret, projectID);
        moodle25_graphql.fetchProductTotalsViaGraphQLandNodes(token, projectID);

    }
}
