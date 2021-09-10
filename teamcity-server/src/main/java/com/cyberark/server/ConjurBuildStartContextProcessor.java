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

public class ConjurBuildStartContextProcessor implements BuildStartContextProcessor {

    // This method will return one SProjectFeatureDescriptior that represents the Cyberark Conjur Connection
    //   provided in the project Connections. This method will return null if no Connection can be found and will throw
    //   a MultipleConnectionsReturnedException if more than one connection was found.
    private SProjectFeatureDescriptor getConnectionType(SProject project, String providerType) throws MultipleConnectionsReturnedException {
        List<SProjectFeatureDescriptor> connections = new ArrayList<SProjectFeatureDescriptor>();
        for (SProjectFeatureDescriptor desc : project.getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE)) {
            String connectionType = desc.getParameters().get(OAuthConstants.OAUTH_TYPE_PARAM);
            if (connectionType.equals(providerType)) {
                connections.add(desc);
            }
        }

        // If no connections were found return null
        if (connections.size() == 0) {
            return null;
        }

        // If more than on connection was found return error
        if (connections.size() > 1 ) {
            throw new MultipleConnectionsReturnedException("Only one CyberArk Conjur Connection should be configured for this project.");
        }

        return connections.get(0);
    }

    private BuildProblemData createBuildProblem(SBuild build, String message) {
        return BuildProblemData.createBuildProblem(build.getBuildNumber(), ConjurSettings.getFeatureType(), message);
    }

    @Override
    public void updateParameters(BuildStartContext context) {
        SRunningBuild build = context.getBuild();
        Logger logger = Loggers.SERVER;
        logger.info("gglog3 from start context processor");
        SBuildType buildType = build.getBuildType();
        if (buildType == null) {
            // It is possible of build type to be null, if this is the case lets return and not retrieve conjur secrets
            return;
        }

        SProject project = buildType.getProject();
        SProjectFeatureDescriptor connectionFeatures = null;

        try {
            connectionFeatures = getConnectionType(project, ConjurSettings.getFeatureType());
        } catch (MultipleConnectionsReturnedException e) {
            BuildProblemData buildProblem = createBuildProblem(build, String.format("ERROR: %s", e.getMessage()));
            build.addBuildProblem(buildProblem);
        }
        
        if (connectionFeatures == null) {
            // If connection feature cannot be found (no connection has been configured on this project)
            // then return and do not perform conjur secret retrieval actions
            return;
        }

        ConjurConnectionParameters conjurConfig = new ConjurConnectionParameters(connectionFeatures.getParameters());

        try {
            for(Map.Entry<String, String> kv : conjurConfig.getAgentSharedParameters().entrySet()) {
                context.addSharedParameter(kv.getKey(), kv.getValue());
            }
        } catch (MissingMandatoryParameterException e) {
            BuildProblemData buildProblem = createBuildProblem(build,
                    String.format("ERROR: Setting agent's shared parameters. %s. %s",
                            e.getMessage(), conjurConfig.toString()));
            build.addBuildProblem(buildProblem);
        }
    }
}
