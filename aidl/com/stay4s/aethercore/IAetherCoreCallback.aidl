package com.stay4s.aethercore;

import com.stay4s.aethercore.AIResponse;

oneway interface IAetherCoreCallback {
    void onResult(in AIResponse response);
    void onError(String errorMessage, int errorCode);
    void onProgress(int percent, String stage);
}
