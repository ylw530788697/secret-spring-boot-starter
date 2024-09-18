package com.secret.starter.kms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.kms.v20190118.KmsClient;
import com.tencentcloudapi.kms.v20190118.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

public class KmsSecret {
    private static final String kmsCredentials = System.getenv("KMS_CREDENTIALS");
    private static final Logger log = LoggerFactory.getLogger(KmsSecret.class);

    private static String secretId;
    private static String secretKey;
    private static String region;
    private static String keyId;
    private static String encryptType;
    private static KmsClient kmsClient = null;

    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> credentials = objectMapper.readValue(kmsCredentials, Map.class);
            secretId = credentials.get("SECRET_ID");
            secretKey = credentials.get("SECRET_KEY");
            region = credentials.get("REGION");
            keyId = credentials.get("KEY_ID");
            encryptType = credentials.get("ENCRYPT_TYPE");
            Credential cred = new Credential(secretId, secretKey);
            kmsClient = new KmsClient(cred, region);
        } catch (Exception e) {
            log.error("KMS Secret Initialization Failed", e);
        }
    }

    public static String encrypt(String plaintext){
        EncryptRequest encryptRequest = new EncryptRequest();
        encryptRequest.setKeyId(keyId);
        String plaintextString = new String(Base64.getEncoder().encode(plaintext.getBytes()));
        encryptRequest.setPlaintext(plaintextString);
        try {
            EncryptResponse encrypt = kmsClient.Encrypt(encryptRequest);
            return encrypt.getCiphertextBlob();
        } catch (TencentCloudSDKException e) {
            log.error("encrypt failed", e);
        }
        return null;
    }

    //解密接口
    public static String dncrypt(String ciphertextBlob){
        if (encryptType.equals(EncryptionEnum.RSAES_PKCS1_V1_5.getCode())) {
            String s = decryptRSA(ciphertextBlob);
            return new String(Base64.getDecoder().decode(s));
        }
        return decryptString(ciphertextBlob);
    }

    private static String decryptString(String cipherTextBlob){
        DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setCiphertextBlob(cipherTextBlob);
        try {
            DecryptResponse decrypt = kmsClient.Decrypt(decryptRequest);
            return decrypt.getPlaintext();
        } catch (TencentCloudSDKException e) {
            log.error("encrypt failed", e);
        }
        return null;
    }

    private static String decryptRSA(String ciphertextBlob){
        AsymmetricRsaDecryptRequest req = new AsymmetricRsaDecryptRequest();
        req.setKeyId(keyId);
        req.setAlgorithm(EncryptionEnum.RSAES_PKCS1_V1_5.getCode());
        req.setCiphertext(ciphertextBlob);
        try {
            AsymmetricRsaDecryptResponse asymmetricRsaDecryptResponse = kmsClient.AsymmetricRsaDecrypt(req);
            return asymmetricRsaDecryptResponse.getPlaintext();
        } catch (TencentCloudSDKException e) {
            log.error("decryptRSA failed", e);
        }
        return null;
    }
    public static void main(String[] args) {
//        DecryptResponse dncrypt1 = KmsSecret.dncrypt("cO47vlfkaNLurKYz82aQ/4QAodgqJRqpNVxDV6rZsRLso4jiA8f8fH0WFPA5k4WWhbvPRxPfFrYGdRvIPjKsZg==-k-fKVP3WIlGpg8m9LMW4jEkQ==-k-EL6M/qB8Y2gGNbCvbd36xeNVAC1L0ez9HZBNqpfhOxLQNG+a");
//        System.out.println(dncrypt1);
        String dncrypt = KmsSecret.dncrypt("TYNnTSdcn1dtXIxgqI1k4wL8bUebJqI1A+QVvmdqxa+dhU93HZUzfd5FO3DZ2BZ0rYp4jx2yueiuwzZOxBxsglNJYWK24xvw0SoGcD4Y4hd9Oi1W4cw76DXIILfbQ7b7pxmG+Vm1PGvxD5G7pB0hhYN0vUjjHNJ1cN1qqrJdo5nZJZgL0hKQD8ssVNEvX0sXOjfz1jHo/iYd0oLMRIg2N4hQI/dN62BY+wBG0c7dEDWm51EnBwtDyf/dHkLQUNl/boqqVd/PjC20ku3G2+TEYLFjvI2S1kPHdmMYxJh4RoHFzNj7SacEe4X37m4wdEJsaSXoR5zxeVKWXuOMNhVEFQ==");
        System.out.println(dncrypt);
        byte[] decode = Base64.getDecoder().decode("aGVsbG93b3JsZA==");
        String s = new String(decode);
        System.out.println(s);
    }

}
