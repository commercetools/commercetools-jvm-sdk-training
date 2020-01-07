package handson.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import handson.json.TokenAnswer;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

public class ThirdPartyClientService {
    private static final Logger LOG = LoggerFactory.getLogger(ThirdPartyClientService.class);


    public String createClientAndFetchToken(String clientID, String clientSecret, String projectID)
    {
        OkHttpClient myAPIClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Response response = null;

        try {
            String encoding = Base64.getEncoder().encodeToString(new String(clientID + ":" + clientSecret).getBytes("UTF-8"));
            RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");

            // Request for token
            Request oAuthRequest = new Request.Builder()
                    .url("https://auth.sphere.io/oauth/token")
                    .post(body)
                    .addHeader("Authorization", "Basic " + encoding)
                    .addHeader("cache-control", "no-cache")
                    .build();

            response = myAPIClient.newCall(oAuthRequest).execute();

            final byte[] bytes = IOUtils.toByteArray(response.body().byteStream());
            String bodyString = new String(bytes, "UTF-8");
            TokenAnswer tokenAnswer = new ObjectMapper().readValue(bodyString, TokenAnswer.class);

            return tokenAnswer.getAccess_token();

        } catch (IOException e) {
            LOG.info("Execption" + e.toString());
        }
        response.body().close();                        // TODO: Not closing properly!!
        return "";
    }

    public String createClientAndFetchMeToken(String clientID, String clientSecret, String projectID, String customerEmail, String customerLogon)
    {

        OkHttpClient myAPIClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Response response = null;

        try {
            String encoding = Base64.getEncoder().encodeToString(new String(clientID + ":" + clientSecret).getBytes("UTF-8"));
            RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=" + customerEmail + "&password=" + customerLogon);

            // Request for token
            Request oAuthRequest = new Request.Builder()
                    .url("https://auth.sphere.io/oauth/" + projectID + "/customers/token")
                    .post(body)
                    .addHeader("Authorization", "Basic " + encoding)
                    .addHeader("cache-control", "no-cache")
                    .build();

            response = myAPIClient.newCall(oAuthRequest).execute();

            final byte[] bytes = IOUtils.toByteArray(response.body().byteStream());
            String bodyString = new String(bytes, "UTF-8");
            TokenAnswer tokenAnswer = new ObjectMapper().readValue(bodyString, TokenAnswer.class);

            return tokenAnswer.getAccess_token();

        } catch (IOException e) {
            LOG.info("Execption" + e.toString());
        }
        return "";
    }

}
