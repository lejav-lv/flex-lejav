package com.hisw.common.encrypt.core.encryptor;

import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.encrypt.core.EncryptContext;
import com.hisw.common.encrypt.enumd.AlgorithmType;
import com.hisw.common.encrypt.enumd.EncodeType;
import com.hisw.common.encrypt.utils.EncryptUtils;


/**
 * RSA算法实现
 *
 * @author lejav
 */
public class RsaEncryptor extends AbstractEncryptor {

    private final EncryptContext context;

    public RsaEncryptor(EncryptContext context) {
        String privateKey = context.getPrivateKey();
        String publicKey = context.getPublicKey();
        if (StringUtils.isAnyEmpty(privateKey, publicKey)) {
            throw new IllegalArgumentException("RSA公私钥均需要提供，公钥加密，私钥解密。");
        }
        this.context = context;
    }

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.RSA;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return EncryptUtils.encryptByRsa(value, context.getPublicKey());
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByRsa(value, context.getPrivateKey());
    }
}
