package com.blockchain;

//import org.stellar.sdk.Network;

import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.xdr.MemoType;

import static com.blockchain.Constants.*;

public class Payment {

    public static void sendMoney_RajeshToAdam() throws Exception {
        //   Network. useTestNetwork();

        Server server = new Server("https://horizon-testnet.stellar.org");

        KeyPair source = KeyPair.fromSecretSeed(RAJESH_SEED_KEY);
        KeyPair destination = KeyPair.fromAccountId(ADAM_PUB_KEY);

        // First, check to make sure that the destination account exists.
        // You could skip this, but if the account does not exist, you will be charged
        // the transaction fee when the transaction fails.
        // It will throw HttpResponseException if account does not exist or there was another error.
        server.accounts().account(destination.getAccountId());

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccountResponse = server.accounts().account(source.getAccountId());

        Account sourceAccount = new Account(source.getAccountId(), sourceAccountResponse.getSequenceNumber());

        // Start building the transaction.
        Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), "10").build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text("Test Transaction"))
                // Wait a maximum of three minutes for the transaction
                .setTimeout(180)
                .build();
// Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);


// And finally, send it off to Stellar!
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            System.out.println("Success!");
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
            // If the result is unknown (no response body, timeout etc.) we simply resubmit
            // already built transaction:
            // SubmitTransactionResponse response = server.submitTransaction(transaction);
        }
    }

    public static void main(String[] args) throws Exception {
        sendMoney_RajeshToAdam();
    }
}
