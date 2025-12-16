package com.dayou.dance.aidl;

interface DanceAidl {
    boolean isDancing();
    void startDance(int type);
    void stopDance();
}