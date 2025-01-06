package com.wanghengtong.framework.license;

import de.schlichtherle.license.AbstractKeyStoreParam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 22:30
 */
public class CustomKeyStoreEntity extends AbstractKeyStoreParam {

    private final String storePath;

    private final String alias;

    private final String storePwd;

    private final String keyPwd;

    public CustomKeyStoreEntity(Class clazz, String resource, String alias, String storePwd, String keyPwd) {
        super(clazz, resource);
        this.storePath = resource;
        this.alias = alias;
        this.storePwd = storePwd;
        this.keyPwd = keyPwd;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getStorePwd() {
        return storePwd;
    }

    @Override
    public String getKeyPwd() {
        return keyPwd;
    }

    @Override
    public InputStream getStream() throws IOException {
        return Files.newInputStream(Paths.get(storePath));
    }

}