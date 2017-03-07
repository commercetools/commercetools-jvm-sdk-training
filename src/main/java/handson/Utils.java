package handson;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;

import java.time.Duration;
import java.util.Date;

class Utils {

    private static String QUEUE_NAME = "testQueue" + new Date().getTime();
    private static String queue_url;

    /**
     * Creates a blocking sphere client
     * @return Sphere client
     */
    static BlockingSphereClient createSphereClient() {
        final String projectKey = "fyayc-hou-42";
        final String clientId = "Olmu4RPsFJZvN82npylM6jII";
        final String clientSecret = "VdELOheKjPbprfWrbG5wrYUZZ01ojARr";

        final SphereClientConfig clientConfig = SphereClientConfig.of(projectKey, clientId, clientSecret);

        return BlockingSphereClient.of(SphereClientFactory.of().createClient(clientConfig), Duration.ofMinutes(1));
    }

    static AmazonSQS createSQSClient(Regions region, String awsProfileName){
        return AmazonSQSClientBuilder.standard()
                .withRegion(region)
                // credentials should be set in ~/.aws/credentials
                .withCredentials(new ProfileCredentialsProvider(awsProfileName))
                .build();
    }

    static void createQueue(Regions region, String awsProfileName){

        AmazonSQS sqs = createSQSClient(region, awsProfileName);
        // create a queue
        CreateQueueRequest cq_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");
        sqs.createQueue(cq_request);

        // Get url of the created queue
        queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
        System.out.println(queue_url);
    }

}