package com.blockchain;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class CreateAccount {

    public static void main(String[] args) throws Exception {
        System.out.println("Blockchain testing");

        KeyPair pair = KeyPair.random();
        /*
        Secret -> SB4ARDMZMMRDKINGGG6SRLQLI3R7P5P4OM6TFSST56IR42TLWYZKTUZO
        public key ->  GADBZNODG4ATATBNREYFEKGIBRFJI5I5FF2T4F7CZ5EA7AYZGKJZHZX4
         */
        System.out.println("Secret -> " + new String(pair.getSecretSeed()));
        System.out.println("public key ->  " + pair.getAccountId());


        String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", pair.getAccountId());
        InputStream response = new URL(friendbotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        System.out.println("SUCCESS! You have a new account :)\n" + body);

        Server server = new Server("https://horizon-testnet.stellar.org");
        AccountResponse account = server.accounts().account(pair.getAccountId());
        System.out.println("Balances for account " + pair.getAccountId());
        for (AccountResponse.Balance balance : account.getBalances()) {
            System.out.println(String.format(
                    "Type: %s, Code: %s, Balance: %s",
                    balance.getAssetType(),
                    balance.getAssetCode(),
                    balance.getBalance()));
        }
    }


}
