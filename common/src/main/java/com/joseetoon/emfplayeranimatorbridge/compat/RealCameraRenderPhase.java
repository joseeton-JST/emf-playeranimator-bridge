package com.joseetoon.emfplayeranimatorbridge.compat;

enum RealCameraRenderPhase {
    NONE,
    REAL_CAMERA_BIND_CAPTURE,
    REAL_CAMERA_BODY_RENDER;

    String debugName() {
        return switch (this) {
            case NONE -> "none";
            case REAL_CAMERA_BIND_CAPTURE -> "bind_capture";
            case REAL_CAMERA_BODY_RENDER -> "body_render";
        };
    }
}
