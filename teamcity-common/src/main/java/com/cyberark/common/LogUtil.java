package com.cyberark.common;
// import com.intellij.openapi.diagnostic.Logger;

import jetbrains.buildServer.agent.BuildProgressLogger;

public class LogUtil {
    protected  BuildProgressLogger agentLogger;
    protected boolean verbose = false;

    public LogUtil(BuildProgressLogger agentLogger, boolean verbose){
        this.agentLogger = agentLogger;
        this.verbose = verbose;
    }

    public void Verbose(String message) {
        if (this.verbose) {
            this.Log("VERBOSE", message);
        }
    }

    public void Log(String level, String message) {
        message = String.format("%s: %s", level, message);
        this.agentLogger.message(message);
    }
}
