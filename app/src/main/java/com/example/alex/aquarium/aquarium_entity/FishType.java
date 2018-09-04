package com.example.alex.aquarium.aquarium_entity;

import java.io.Serializable;
import java.util.HashMap;

public class FishType implements Serializable {

    String mName;
    float mSpeedWalk;
    float mSpeedRun;
    float mVision;
    int mBirthFrequency;
    int mLifetime;
    int mMaxCount;
    boolean mSettled;

    float mStartFishSize;
    float mMaxFishSize;

    // взаимоотношения с другими типами рыб
    // чем больше модуль, тем выше приоритет.
    // 0 - нейтральные. 1, 2, 3... - первая охотится на них. -1, -2, -3... - первая убегает от них.
    HashMap<FishType, Integer> mDiplomacy = new HashMap<>();

    public FishType(String name, float speedWalk, float speedRun, float vision,
                    int birthFrequency, int lifetime, int maxCount, boolean settled, float sfs, float mfs) {
        mName = name;
        mSpeedWalk = speedWalk;
        mSpeedRun = speedRun;
        mVision = vision;
        mBirthFrequency = birthFrequency;
        mLifetime = lifetime;
        mMaxCount = maxCount;
        mSettled = settled;

        mStartFishSize = sfs;
        mMaxFishSize = mfs;
    }

    public void setDiplomacyStatus(FishType fishType, int status) {
        mDiplomacy.put(fishType, status);
    }

    public int getDiplomacyStatus(FishType fishType) {
        Integer status = mDiplomacy.get(fishType);
        return status == null ? 0 : status;
    }

    public String getName() {
        return mName;
    }

    public int getMaxCount() {
        return mMaxCount;
    }

}