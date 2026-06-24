package com.joseetoon.emfplayeranimatorbridge.compat;

final class RenderSkipState {
    private static final ThreadLocal<Boolean> SKIP_NEXT_MAIN_RENDER = new ThreadLocal<>();

    private RenderSkipState() {
    }

    static void arm() {
        SKIP_NEXT_MAIN_RENDER.set(Boolean.TRUE);
    }

    static boolean consume() {
        Boolean skip = SKIP_NEXT_MAIN_RENDER.get();
        if (Boolean.TRUE.equals(skip)) {
            SKIP_NEXT_MAIN_RENDER.remove();
            return true;
        }
        return false;
    }

    static void clear() {
        SKIP_NEXT_MAIN_RENDER.remove();
    }
}
