package com.stay4s.aethercore;

parcelable AIResponse {
    int status;
    String result;
    String source;
    long latencyMs;
    String errorMessage;
    Bundle metadata;
}
