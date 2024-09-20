package com.hisw.common.encrypt.filter;

import cn.hutool.core.io.IoUtil;
import com.hisw.common.encrypt.utils.EncryptUtils;
import com.hisw.common.redis.utils.RedisUtils;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 解密请求参数工具类
 *
 * @author lejav
 */
public class DecryptRequestBodyWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public DecryptRequestBodyWrapper(HttpServletRequest request, String privateKey) throws IOException {
        super(request);
        // 获取 AES 密码 采用 RSA 加密
        String privateKeyValue = RedisUtils.getCacheMapValue("loginRsa", privateKey);
        byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
        String requestBody = new String(readBytes, StandardCharsets.UTF_8);
        // 解密 body 采用 AES 加密
        String decryptBody = EncryptUtils.decryptByRsa(requestBody, privateKeyValue);
        body = UriUtils.decode(decryptBody, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
