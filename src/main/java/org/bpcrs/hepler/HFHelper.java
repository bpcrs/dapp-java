package org.bpcrs.hepler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bpcrs.object.HFUser;
import org.bpcrs.object.RequestType;
import org.bpcrs.object.ResultChaincode;
import org.bpcrs.object.UserCertificate;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class HFHelper {
    private static final String PEM_FILE_PATH = System.getProperty("pem.file");
    private static final String CA_URL = System.getProperty("ca.url");
    private static final String ADMIN_USERNAME = "admin";
    private static final String NETWORK_PATH = System.getProperty("network.config.path");

    // Create a CA client for interacting with the CA.
    public static HFCAClient getCAClient() throws Exception {
        Properties props = new Properties();
        props.put("pemFile", PEM_FILE_PATH);
        props.put("allowAllHostNames", true);
        HFCAClient hfcaClient = HFCAClient.createNewInstance(CA_URL, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        hfcaClient.setCryptoSuite(cryptoSuite);
        return hfcaClient;
    }

    public static void registerUser(String username) throws Exception {
        HFCAClient caClient = HFHelper.getCAClient();
        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Check to see if we've already enrolled the user.
        boolean userExists = wallet.get(username) != null;
        if (userExists) {
            System.out.println("An identity for the user \"" + username + "\" already exists in the wallet");
            return;
        }

        userExists = wallet.get(ADMIN_USERNAME) != null;
        if (!userExists) {
            System.out.println("\"" + ADMIN_USERNAME + "\" needs to be enrolled and added to the wallet first");
            return;
        }

        HFUser adminUser = new HFUser("wallet", ADMIN_USERNAME);

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(username);
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(username);
        String enrollmentSecret = caClient.register(registrationRequest, adminUser);

        Enrollment enrollment = caClient.enroll(username, enrollmentSecret);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put(username, user);
        System.out.println("Successfully registered user \"" + username + "\" and imported it into the wallet");
    }

    public static UserCertificate loadFromFile(String username, String certFolder) throws Exception {
        Path path = Paths.get(certFolder, username + ".id");
        if (!path.toFile().exists()) return null;
        File file = new File(path.toString());
        return new ObjectMapper().readValue(file, UserCertificate.class);
    }

    public static ResultChaincode processRequest(RequestType type, String username, String channel,
                                                 String chaincodeId, String funcName,
                                                 String... args) {
        // Load a file system based wallet for managing identities.
        ResultChaincode resultChaincode = new ResultChaincode();
        Path walletPath = Paths.get("wallet");
        Wallet wallet = null;
        try {
            wallet = Wallets.newFileSystemWallet(walletPath);
        } catch (IOException e) {
            resultChaincode.setMessage("[wallet] folder notfound in system");
        }
        // load a CCP
        Path networkConfigPath = Paths.get(NETWORK_PATH);

        Gateway.Builder builder = Gateway.createBuilder();

        try {
            wallet.get(username);
        } catch (IOException e) {
            resultChaincode.setMessage("An identity for the user [" + username + "] not exists in the wallet");
            return resultChaincode;
        }

        try {
            builder.identity(wallet, username).networkConfig(networkConfigPath).discovery(true);
        } catch (IOException e) {
            resultChaincode.setMessage("Network Configuration not found");
        }
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(channel);
            if (network == null) {
                resultChaincode.setMessage("Channel [" + channel + "] not found");
            }
            Contract contract = network.getContract(chaincodeId);

            byte[] result;
            if (type == RequestType.INVOKE) {
                result = contract.submitTransaction(funcName, args);
                resultChaincode.setData(result.toString());
                resultChaincode.setMessage("Invoke Successful");
            } else if (type == RequestType.QUERY) {
                result = contract.evaluateTransaction(funcName, args);
                System.out.println(result);
                resultChaincode.setData(new String(result));
                resultChaincode.setMessage("Query Successful");
            }
        } catch (Exception e) {
            resultChaincode.setMessage(e.getMessage());
        }
        return resultChaincode;
    }
}
