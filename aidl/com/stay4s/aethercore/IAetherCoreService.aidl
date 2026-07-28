package com.stay4s.aethercore;

import com.stay4s.aethercore.TaskRequest;
import com.stay4s.aethercore.AIResponse;
import com.stay4s.aethercore.IAetherCoreCallback;

interface IAetherCoreService {
    AIResponse submitTask(in TaskRequest request);
    void submitTaskAsync(in TaskRequest request, in IAetherCoreCallback callback);
    String queryAgent(String agentId, in Bundle args);
    boolean isLocalModelAvailable(String modelId);
    void preloadModel(String modelId);
    int getModelState();
    void setCloudAllowed(boolean allowed);
    boolean isCloudAvailable();
    boolean isCloudAllowed();
    String registerClient(String name, int apiVersion);
    void unregisterClient(String clientId);
    String[] getCapabilities();
    boolean ping();
}
