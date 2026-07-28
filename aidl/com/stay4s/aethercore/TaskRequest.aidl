package com.stay4s.aethercore;

parcelable TaskRequest {
    String agentId;
    String action;
    Bundle params;
    int privacyLevel;
    long timeoutMs;
    String clientId;
}
