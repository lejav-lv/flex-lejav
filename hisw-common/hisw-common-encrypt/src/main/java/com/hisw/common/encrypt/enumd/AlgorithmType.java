package com.hisw.common.encrypt.enumd;

import com.hisw.common.encrypt.core.encryptor.AbstractEncryptor;
import com.hisw.common.encrypt.core.encryptor.Base64Encryptor;
import com.hisw.common.encrypt.core.encryptor.RsaEncryptor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 算法名称
 *
 * @author lejav
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

    /**
     * 默认走yml配置
     */
    DEFAULT(null),

    /**
     * base64
     */
    BASE64(Base64Encryptor.class),

    /**
     * rsa
     */
    RSA(RsaEncryptor.class);

    private final Class<? extends AbstractEncryptor> clazz;
}
