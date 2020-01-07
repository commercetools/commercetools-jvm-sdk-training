package handson;

import handson.impl.ThirdPartyClientService;
import io.sphere.sdk.client.SphereClient;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;



// TODO: Use a different HTTP client, like retrofit
//
public class Moodle19_METOKEN_TEST {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle19_METOKEN_TEST.class);


    public void getMeOrders(String meToken, String projectID) {

        OkHttpClient myAPIClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Response response = null;

        try {

            Request oAuthRequest = new Request.Builder()
                    // TODO: specify url, method, Headers, cache-control
                    .build();

            response = myAPIClient.newCall(oAuthRequest).execute();

            final byte[] bytes = IOUtils.toByteArray(response.body().byteStream());
            String bodyString = new String(bytes, "UTF-8");
            LOG.info("Token Answer: " + bodyString);

        } catch (IOException e) {
            LOG.info("Execption" + e.toString());
        }
        response.body().close();
    }


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

            Moodle19_METOKEN_TEST task = new Moodle19_METOKEN_TEST();

            // TODO: Test the meOrders endpoint
            //
            String clientID = "5HEQ28SkOxxAad7q34oTA1G6";                   // TODO Parse from dev.properties
            String clientSecret = "y2dPYAB2HckRpMcPlxkir8Zdv_iLA1Ks";
            String projectID = "training-etribes-dec2019-def";

            // CustomerEmail & Password
            // Encode Base64 by Hand !!
            String customerEmail = "michaelhartwig1%40example.com";
            String customerLogon = "password";

            ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();
            // TODO: Inspect
            String metoken = thirdPartyClientService.createClientAndFetchMeToken(clientID, clientSecret, projectID, customerEmail, customerLogon);

            task.getMeOrders(metoken, projectID);
    }
}
