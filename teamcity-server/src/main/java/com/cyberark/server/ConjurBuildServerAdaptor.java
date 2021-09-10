package com.cyberark.server;

import com.cyberark.common.exceptions.ConjurApiAuthenticateException;
import com.cyberark.common.exceptions.MissingMandatoryParameterException;
import com.cyberark.common.exceptions.MultipleConnectionsReturnedException;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.oauth.OAuthConstants;
import jetbrains.buildServer.serverSide.buildLog.*;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.log.*;
import jetbrains.buildServer.serverSide.buildLog.*;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.ssl.SSLTrustStoreProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;
import com.intellij.openapi.diagnostic.Logger;
import com.cyberark.common.*;


public class CustomServerAdaptor extends BuildServerAdapter {
    public EventDispatcher<BuildServerListener> dispatcher;
    public SSLTrustStoreProvider trustStoreProvider;

    public CustomServerAdaptor(EventDispatcher<BuildServerListener> dispatcher, SSLTrustStoreProvider trustStoreProvider){
        this.dispatcher = dispatcher;
        this.trustStoreProvider = trustStoreProvider;
        this.dispatcher.addListener(this);
    }

    @Override
    public void buildStarted(SRunningBuild build){
        Logger logger = Loggers.SERVER;
        logger.info("logging from build server adaptor");

        Map<String, String> properties = build.getBuildOwnParameters();

        for (Map.Entry<String, String> kv : properties.entrySet() ) {
              logger.info("-----logging for params-------");
              logger.info(kv.getKey()+":"+kv.getValue());
          }
    }
    
}