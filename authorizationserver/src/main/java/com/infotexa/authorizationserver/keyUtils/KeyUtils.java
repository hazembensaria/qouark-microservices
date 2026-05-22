package com.infotexa.authorizationserver.keyUtils;

import com.infotexa.authorizationserver.exception.ApiException;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Slf4j
@Configuration
public class KeyUtils {
    private static final String RSA = "RSA";

        @Value("${spring.profiles.active}")
        private String activeProfile ;

        @Value("${keys.private}")
        private String privateKey ;

        @Value("${keys.public}")
        private String publicKey ;

    public RSAKey getRsaKey() {
        return generateRSAKeyPair(privateKey, publicKey);
    }
    private RSAKey generateRSAKeyPair(String privateKeyName, String publicKeyName) {
        try {
            var keyFactory = KeyFactory.getInstance(RSA);

            // load public key from classpath (inside the jar)
            byte[] publicKeyBytes = new ClassPathResource("keys/" + publicKeyName)
                    .getInputStream()
                    .readAllBytes();
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory
                    .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            // load private key from classpath (inside the jar)
            byte[] privateKeyBytes = new ClassPathResource("keys/" + privateKeyName)
                    .getInputStream()
                    .readAllBytes();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            log.info("RSA keys loaded successfully from classpath");
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();

        } catch (Exception e) {
            log.error("Failed to load RSA keys: {}", e.getMessage());
            throw new ApiException("Failed to load RSA keys: " + e.getMessage());
        }
    }

//    private RSAKey generateRSAKeyPair(String privateKeyName , String publicKeyName)  {
//        KeyPair keyPair;
//        var keysDirectory = Paths.get("src", "main", "resources", "keys");
//        verifyKeysDirectory(keysDirectory);
//        if ( Files.exists(keysDirectory.resolve(privateKeyName)) && Files.exists(keysDirectory.resolve(publicKeyName))) {
//            log.info("Keys already exist. Loading from files.");
//            var privateKeyFile = keysDirectory.resolve(privateKeyName).toFile();
//            var publicKeyFile = keysDirectory.resolve(publicKeyName).toFile();
//            try {
//                var keyFactory = KeyFactory.getInstance(RSA);
//                byte[] publicKeyBites = Files.readAllBytes(publicKeyFile.toPath());
//                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBites);
//                RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
//
//                byte[] privateKeyBites = Files.readAllBytes(privateKeyFile.toPath());
//                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBites);
//                RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
//
//                var keyId = UUID.randomUUID().toString();
//                log.info("Keys loaded successfully with keyId: {}", keyId);
//                return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(keyId).build();
//            } catch (Exception e) {
//                log.error("Error loading keys: {}", e.getMessage());
//                throw new ApiException("Failed to load keys: " + e.getMessage());
//            }
//        }else {
//            log.info("Generating new RSA key pair.");
//
//        }
//
//       try {
//           var keyPairGenerator = KeyPairGenerator.getInstance(RSA);
//           keyPairGenerator.initialize(2048);
//           keyPair = keyPairGenerator.generateKeyPair();
//           RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//           RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//
//           try(var fos = new FileOutputStream(keysDirectory.resolve(publicKeyName).toFile())){
//               X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
//               fos.write(keySpec.getEncoded());
//           }
//
//           try(var fos = new FileOutputStream(keysDirectory.resolve(privateKeyName).toFile())) {
//               PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
//               fos.write(keySpec.getEncoded());
//               return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
//           }
//       } catch (Exception e) {
//           throw new ApiException(e.getMessage());
//       }
//    }



    private void verifyKeysDirectory(Path keysDirectory){
        if(!Files.exists(keysDirectory)){
            try {
                Files.createDirectories(keysDirectory);
            }catch (Exception e){
                throw new ApiException("Failed to create keys directory: " + e.getMessage());
            }
            log.info("Keys directory created at: {}", keysDirectory);
        }
    }
}
