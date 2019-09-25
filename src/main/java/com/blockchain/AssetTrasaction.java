package com.blockchain;

import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.xdr.MemoType;

import static com.blockchain.Constants.*;


public class AssetTrasaction {

    private static final Server STELLAR_SERVER = new Server("https://horizon-testnet.stellar.org");
    // Keys for accounts to issue and receive the new asset
    private static KeyPair issuingKeys = KeyPair.fromSecretSeed(RAJESH_SEED_KEY);
    private static KeyPair receivingKeys = KeyPair.fromSecretSeed(ADAM_SEED_KEY);


    public static void createHomeAsset() throws Exception {

        // Create an object to represent the new asset
        Asset astroDollar = Asset.createNonNativeAsset("HOME", issuingKeys.getAccountId());


        // First, the receiving account must trust the asset
        AccountResponse receivingAccountResponse = STELLAR_SERVER.accounts().account(receivingKeys.getAccountId());

         //receivingAccountResponse.

        Account receivingAccount = new Account(receivingKeys.getAccountId(), receivingAccountResponse.getSequenceNumber());


        // --------------------
        Transaction allowAstroDollars = new Transaction.Builder(receivingAccount, Network.TESTNET)
                .addOperation(
                        // The `ChangeTrust` operation creates (or alters) a trustline
                        // The second parameter limits the amount the account can hold
                        new ChangeTrustOperation.Builder(astroDollar, "1000").build())
                .setTimeout(60)
                .build();
        allowAstroDollars.sign(receivingKeys);
        STELLAR_SERVER.submitTransaction(allowAstroDollars);


        System.out.println("Sucess");
    }

    public static Asset createHomeAsset_Rajesh() {
        // Create an object to represent the new asset
        Asset myHomeAsset = Asset.createNonNativeAsset("HOME03", issuingKeys.getAccountId());
        System.out.println("home asset -> " + myHomeAsset.getType());
        return myHomeAsset;

    }

    public static Asset createHomeAsset_Adam() {
        // Create an object to represent the new asset
        Asset myHomeAsset = Asset.createNonNativeAsset("HOME01", issuingKeys.getAccountId());
        System.out.println("home asset -> " + myHomeAsset.getType());
        return myHomeAsset;

    }


    public static void trustIssuerAsset() throws Exception {
        // First, the receiving account must trust the asset
        AccountResponse receivingAccountResponse = STELLAR_SERVER.accounts().account(receivingKeys.getAccountId());

        System.out.println("Seq No -> " + receivingAccountResponse.getSequenceNumber());
        Account receivingAccount = new Account(receivingKeys.getAccountId(),
                receivingAccountResponse.getSequenceNumber()+1);

        Asset homeAssetRajesh =  createHomeAsset_Rajesh();
        Transaction allowHomeAssetTrx = new Transaction.Builder(receivingAccount, Network.TESTNET)
                .addOperation(
                        // The `ChangeTrust` operation creates (or alters) a trustline
                        // The second parameter limits the amount the account can hold
                        new ChangeTrustOperation.Builder(homeAssetRajesh, "1000").build())
                //.addMemo(Memo.text("Home Asset"))
                // Wait a maximum of 1 minutes for the transaction
                .setTimeout(60)
                .setOperationFee(100)
                .build();
        allowHomeAssetTrx.sign(receivingKeys);

        STELLAR_SERVER.submitTransaction(allowHomeAssetTrx);


        // Second, the issuing account actually sends a payment using the asset
        AccountResponse issuingAccountResponse = STELLAR_SERVER.accounts().account(issuingKeys.getAccountId());
        Account issuingAccount = new Account(issuingKeys.getAccountId(),
                issuingAccountResponse.getSequenceNumber()+1);

        Transaction sendAstroDollars = new Transaction.Builder(issuingAccount,Network.TESTNET)
                .addOperation(
                        new PaymentOperation.Builder(receivingKeys.getAccountId(), homeAssetRajesh, "10").build())
                .setTimeout(60)
                .setOperationFee(100)
                .build();
        sendAstroDollars.sign(issuingKeys);

        SubmitTransactionResponse response =STELLAR_SERVER.submitTransaction(sendAstroDollars);

        System.out.println("Result -> " +response.getDecodedTransactionResult().get());
    }

    public static void main(String[] args) throws Exception {
        createHomeAsset_Rajesh();
        trustIssuerAsset();
        System.out.println("Done Successfully");
    }
}
