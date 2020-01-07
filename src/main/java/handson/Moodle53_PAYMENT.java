package handson;

import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.OrderService;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddPayment;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.queries.ChannelByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.queries.CustomerByKeyGet;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.orders.OrderState;
import io.sphere.sdk.payments.*;
import io.sphere.sdk.payments.commands.PaymentCreateCommand;
import io.sphere.sdk.payments.commands.PaymentUpdateCommand;
import io.sphere.sdk.payments.commands.updateactions.*;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;


// TODO: Use a different HTTP client, like retrofit
//
public class Moodle53_PAYMENT {
    private static final Logger LOG = LoggerFactory.getLogger(Moodle53_PAYMENT.class);

        public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {
            final CustomerService customerService = new CustomerService(client);
            final CartService cartService = new CartService(client);
            final OrderService orderService = new OrderService(client);


            // Fetch a channel if cart inventory mode is not NONE
            //
            Channel channel = client.execute(
                    ChannelByIdGet.of("6186a257-c7c3-4417-a02c-9ee26093c392")
            )
                    .toCompletableFuture().get();



            // Step -2: Prepare some custom fields for storing payment related information
            //
            List<FieldDefinition> definitions = Arrays.asList(
                    FieldDefinition.of(
                            LocalizedStringFieldType.of(),
                            "TransactionWireCard",
                            LocalizedString.of(
                                    Locale.ENGLISH, "Transaction Info", Locale.GERMAN, "Transaktionsinformation"
                            ),
                            false,
                            TextInputHint.SINGLE_LINE)
            );

            Set<String> resourceTypeIds = ResourceTypeIdsSetBuilder.of()
                    .addPaymentInterfaceInteractions()
                    .build();

            final Type customType_WireCard =
                    client.execute(
                        TypeCreateCommand.of(
                            TypeDraftBuilder.of(
                                    "payment-transaction-wirecard",
                                    LocalizedString.ofEnglish("Transaktionsdaten WireCard"), resourceTypeIds)
                                    .fieldDefinitions(definitions)
                                    .build()
                        )
                    )
                    .toCompletableFuture().get();


            // Add a unique number for different tryouts in case customer plays with different payment methods in different browser tabs
            // This number will then be ignored in the order number if payment goes through
            //
            int unique_extension = 1;

            // Step -1: Create a cart and add a product to it
            //

            Cart cart = client.execute(CustomerByKeyGet.of("michael-hartwig-key1"))
                    .thenComposeAsync(cartService::createCart)
                    .thenComposeAsync(c -> cartService.addProductToCartBySkusAndChannel(c, channel, "987"))
                    .toCompletableFuture().get();

            // Step 0: Customer decides to go for payment
            // Generation of a unique id.
            // Every Payment Provider requires a unique id. (Often called orderID)
            // Problem: We often create order AFTER payment.
            // Problem: Some customer play with multiple payment options in different tabs.
            // Possible solution: Create a unique order id per payment try like order8373465-1, order8373465-2 .. and never show the last part (-1, -2) to the customer
            //
            String orderID = "order-" + unique_extension;

            // Step 1: Storefront displays payment options, customer choses
            // Storefront talks directly to psp about the different payment methods and displays the options
            // Or the store renders hard coded payment methods
            // Customer decides for payment.


            // There is nothing to do in the backend.
            //


            //
            // Step 2: Storefront talks to psp, we create payment object
            //
            // https://github.com/commercetools/commercetools-jvm-sdk
            // Examples commands: https://github.com/commercetools/commercetools-jvm-sdk/tree/master/commercetools-models/src/test/java/io/sphere/sdk/payments/commands
            // totalAmount aus dem cart holen
            // final String anonymousId = randomString();
            // not necessary, or set customer
            //.anonymousId(anonymousId);
            final Payment payment =
                    client.execute(
                            PaymentCreateCommand.of(
                                    PaymentDraftBuilder.of(cart.getTotalPrice())
                                        .paymentMethodInfo(
                                                PaymentMethodInfoBuilder.of()
                                                    .paymentInterface("WIRECARD")    // PSP Provider: WireCard, ....
                                                    .method("CREDIT_CARD")
                                                    .build())
                                        .build()
                            )
                    )
                    .toCompletableFuture().get();


            // Step 3
            // Payment is done via Storefront or API extension
            // we get the information
            //
            String paymentServiceID = "payment" + unique_extension;
            String paymentServiceURL = "http://superpay";


            // Step 4
            // Store payments' unique id on the payment object
            // It is the only link between payment on the psp and our payment object
            //
            final Payment paymentWithID =
                    client.execute(
                            PaymentUpdateCommand.of(payment,
                                    SetInterfaceId.of(paymentServiceID)
                            )
                    )
                    .toCompletableFuture().get();


            // Step 4b
            // store other payment related info on the payment object
            // urls, connection info,...
            //
            // final Payment paymentWithID_and_info = ... paymentServiceURL and more


            // Step 4c
            // Store the payment info on the cart, otherwise the cart has no connection to that payment
            //
            Cart cartWithPayment =
                    client.execute(
                            CartUpdateCommand.of(cart,
                                    AddPayment.of(Payment.referenceOfId(paymentWithID.getId()))
                            )
                    )
                    .toCompletableFuture().get();


            // Step 5: Customer keys in CreditCard info etc..
            // we wait
            String paymentServiceCreditCardUsed = "1234-XXXXXXXXXXXXXXX";

            // Step 6: Storefront or API ext or ... creates a transaction with PSP
            //
            String interactionId = "charged" + unique_extension;
            String transactionInfo = "Everything went well.";


            // Step 7: Store the result of the transaction with the PSP at the payment object
            //
            final Payment paymentWithCharge =
                    client.execute(
                            PaymentUpdateCommand.of(paymentWithID,
                                AddTransaction.of(
                                        TransactionDraftBuilder
                                            .of(TransactionType.CHARGE, cart.getTotalPrice(), ZonedDateTime.now())		// or of type AUTHORIZATION(=Reservation)
                                            .timestamp(ZonedDateTime.now())
                                            .interactionId(interactionId)
                                            .build()
                                )
                            )
                    )
                    .toCompletableFuture().get();

            // Step 8) Logging all talking to psp
            // add all InterfaceInteractions
            // very different per PSP, often a lot of custom fields are now filled
            //
            final Payment paymentWithChargeAndLogging =
                    client.execute(
                            PaymentUpdateCommand.of(paymentWithCharge,
                                    AddInterfaceInteraction.ofTypeIdAndObjects(customType_WireCard.getId(),     // TODO check: Correct ID?
                                        Collections.singletonMap("TransactionWireCard", transactionInfo))
                            )
                    )
                    .toCompletableFuture().get();


            // Often payment means to react to asynchronous incoming notifications.
            // Log all those notifications as interfaceInteractions.
            // b) Plus Status vom Payment evtl. ändern. Wie interpretiert man das hängt vom UseCase ab. Man kann für ein payment PENDING bekommen, dann später SUCCESS (oder FAILURE).
            //
            // Step 9: We get a success asychronously.
            //
            final Payment paymentWithChargeloggingAndtatus =
                    client.execute(
                            PaymentUpdateCommand.of(paymentWithChargeAndLogging,
                                    Arrays.asList(
                                            SetStatusInterfaceCode.of("SUCCESS"),
                                            SetStatusInterfaceText.of("We got the money.")
                                    )
                            )
                    )
                    .toCompletableFuture().get();


            // Step 9) Order erzeugen aus dem cart
            //
            LOG.info("Created order {}",
                    orderService.createOrder(cartWithPayment)
                    .thenComposeAsync(order -> orderService.changeState(order, OrderState.COMPLETE))
                    .toCompletableFuture().get()
            );

        }
    }
}
