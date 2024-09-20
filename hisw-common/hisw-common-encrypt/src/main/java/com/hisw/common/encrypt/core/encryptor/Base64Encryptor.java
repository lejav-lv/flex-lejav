package com.hisw.common.encrypt.core.encryptor;

import com.hisw.common.encrypt.enumd.AlgorithmType;
import com.hisw.common.encrypt.enumd.EncodeType;
import com.hisw.common.encrypt.utils.EncryptUtils;

/**
 * Base64算法实现
 *
 * @author lejav
 */
public class Base64Encryptor extends AbstractEncryptor {

    /**
     * 获得当前算法
     */
    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.BASE64;
    }

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     */
    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return EncryptUtils.encryptByBase64(value);
    }

    /**
     * 解密
     *
     * @param value      待加密字符串
     */
    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByBase64(value);
    }
}
