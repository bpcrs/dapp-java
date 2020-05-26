package org.bpcrs.hepler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bpcrs.object.HFUser;
import org.bpcrs.object.UserCertificate;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class HFHelper {
    private static final String PEM_FILE_PATH = System.getProperty("pem.file");
    private static final String CA_URL = System.getProperty("ca.url");
    private static final String ADMIN_USERNAME = "admin";

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

        HFUser adminUser = new HFUser("wallet",ADMIN_USERNAME);

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(username);
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(username);
        String enrollmentSecret = caClient.register(registrationRequest, adminUser);

        Enrollment enrollment = caClient.enroll(username, enrollmentSecret);
        Identity user = Identities.newX509Identity("Org1MSP",enrollment);
        wallet.put(username, user);
        System.out.println("Successfully registered user \"" + username +"\" and imported it into the wallet");
    }

    public static UserCertificate loadFromFile(String username, String certFolder) throws Exception{
        Path path = Paths.get(certFolder,username + ".id");
        if (!path.toFile().exists()) return null;
        File file = new File(path.toString());
        return new ObjectMapper().readValue(file,UserCertificate.class);
    }
}
