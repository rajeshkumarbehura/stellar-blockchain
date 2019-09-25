package com.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import static com.blockchain.Constants.ADAM_SEED_KEY;
import static com.blockchain.Constants.RAJESH_SEED_KEY;

public class AssetManageDataOperation {


    private static final Server STELLAR_SERVER = new Server("https://horizon-testnet.stellar.org");
    // Keys for accounts to issue and receive the new asset
    private static KeyPair RAJESH_ACCOUNT = KeyPair.fromSecretSeed(RAJESH_SEED_KEY);
    private static ObjectMapper mapper = new ObjectMapper();


    public static void manageData() throws Exception {

        // First, the receiving account must trust the asset
        AccountResponse receivingAccountResponse = STELLAR_SERVER.accounts().account(RAJESH_ACCOUNT.getAccountId());

        Account receivingAccount = new Account(RAJESH_ACCOUNT.getAccountId(), receivingAccountResponse.getSequenceNumber());

        System.out.println("receivingAccount -> " + mapper.writeValueAsString(receivingAccountResponse.getBalances()));


        Transaction allowAstroDollars = new Transaction.Builder(receivingAccount, Network.TESTNET)
                .addOperation(new ManageDataOperation.Builder("laptopOwner", "Rajesh".getBytes()).build())
                .setTimeout(60)
                .setOperationFee(100)
                .build();

        allowAstroDollars.sign(RAJESH_ACCOUNT);

        SubmitTransactionResponse trxResponse  = STELLAR_SERVER.submitTransaction(allowAstroDollars);

        System.out.println("Trasaction Response -> "+trxResponse.getHash());
        System.out.println("Trasaction XDF -> "+trxResponse.getEnvelopeXdr().get());

        receivingAccountResponse = STELLAR_SERVER.accounts().account(RAJESH_ACCOUNT.getAccountId());

        System.out.println("receivingAccount -> " + mapper.writeValueAsString(receivingAccountResponse.getData()));

        byte[] bytes = receivingAccountResponse.getData().getDecoded("Name2");

        System.out.println(     receivingAccountResponse.getData().getDecoded("Name2"));

        String s = new String(bytes);
        System.out.println("Text Decryted : " + s);

    }

    public static void main(String[] args) throws Exception {
        manageData();
        System.out.println("Sucess");
    }
}
