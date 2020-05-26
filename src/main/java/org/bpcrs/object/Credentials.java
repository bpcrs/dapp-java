package org.bpcrs.object;

import javax.xml.bind.DatatypeConverter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Credentials {
    private String certificate;
    private String privateKey;

    public Credentials() {
    }

    public String getCertificate() {
        return certificate;
    }


    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----","").replace("-----END PRIVATE KEY-----","").replaceAll("\n","").replaceAll("\r","");
        byte[] keyData = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(keySpec);
    }

}
