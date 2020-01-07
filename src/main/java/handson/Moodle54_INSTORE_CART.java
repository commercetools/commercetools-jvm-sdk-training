package handson;

import handson.impl.ThirdPartyClientService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.stores.StoreDraftBuilder;
import io.sphere.sdk.stores.commands.StoreCreateCommand;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import org.json.simple.JSONObject;
import static handson.impl.ClientService.createSphereClient;


// TODO: Use a different HTTP client, like retrofit
//
public class Moodle54_INSTORE_CART {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle54_INSTORE_CART.class);

    public void createCart(String meToken, String projectID) {

        OkHttpClient myAPIClient = new OkHttpClient();
        Response response = null;

        try {
            JSONObject json = new JSONObject();
            json.put("currency", "EUR");                            // creating a simple MyCartDraft, Minimum currency

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request oAuthRequest = new Request.Builder()
                    .url("https://api.sphere.io/" + projectID + "/in-store/key=berlin-store/me/carts")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + meToken)
                    .addHeader("cache-control", "no-cache")
                    .build();

            response = myAPIClient.newCall(oAuthRequest).execute();

            final byte[] bytes = IOUtils.toByteArray(response.body().byteStream());
            String bodyString = new String(bytes, "UTF-8");
            LOG.info("Answer: " + bodyString);

        } catch (IOException e) {
            LOG.info("Execption" + e.toString());
        }
        response.body().close();


    }

    public void createStore() {
        try (final SphereClient client = createSphereClient()) {

            LOG.info("Store created: {}",
                    client.execute(
                            StoreCreateCommand.of(
                                    StoreDraftBuilder.of("berlin-store")
                                        .name(LocalizedString.ofEnglish("BerlinENG").plus(Locale.US, "BerlinUS"))
                                        .build()
                            )
                    )
                    .toCompletableFuture().get()
            );

        }
        catch (IOException | InterruptedException | ExecutionException ex) {
            LOG.info("Execption" + ex.toString());
        }

    }




        public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            Moodle54_INSTORE_CART exerciseMoodle54_instore_cart = new Moodle54_INSTORE_CART();

            // Do step by step
            // for demo purposes only
            //

            // TODO Step 1: create a store
            //
            // exerciseMoodle54_instore_cart.createStore();



            // TODO Step 2: Get a store specific api client from MC
            //


            // TODO Step 3: Create an in-store cart
            //

            String clientID = "5HEQ28SkOxxAad7q34oTA1G6";                   // TODO Parse from dev.properties
            String clientSecret = "y2dPYAB2HckRpMcPlxkir8Zdv_iLA1Ks";
            String projectID = "training-etribes-dec2019-def";

            // CustomerEmail & Password
            // Encode Base64 by Hand !!
            String customerEmail = "michaelhartwig1%40example.com";
            String customerLogon = "password";

            ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();

            //String metoken = thirdPartyClientService.createClientAndFetchMeToken(clientID, clientSecret, projectID, customerEmail, customerLogon);
            //exerciseMoodle54_instore_cart.createCart(metoken, projectID);

        }
    }
}
