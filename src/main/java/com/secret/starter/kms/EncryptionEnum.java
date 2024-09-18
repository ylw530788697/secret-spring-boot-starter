package com.secret.starter.kms;


public enum EncryptionEnum {

    AES("AES", "AES加解密"),
    SYMMETRY("SYMMETRY", "对称加密"),
    RSAES_PKCS1_V1_5("RSAES_PKCS1_V1_5", "RSA"),
    RSAES_OAEP_SHA_1("RSAES_OAEP_SHA_1", "RSA"),
    RSAES_OAEP_SHA_256("RSAES_OAEP_SHA_256", "RSA");

    EncryptionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    private String code;

    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }








}
