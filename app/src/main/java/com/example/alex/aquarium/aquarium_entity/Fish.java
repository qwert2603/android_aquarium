package com.example.alex.aquarium.aquarium_entity;

import android.graphics.PointF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Fish implements Serializable {

    FishType mFishType;
    Aquarium mAquarium;

    float mX;
    float mY;
    float mA;

    float mSize;

    int mDeathTime;

    // гонется ли кто-то уже за этой рыбой.
    // чтобы много не гонялось за одной.
    boolean mChased = false;

    boolean mStepDone = false;

    public Fish(Aquarium aquarium, FishType fishType, float x, float y, float a) {
        mAquarium = aquarium;
        mFishType = fishType;
        mX = x;
        mY = y;
        mA = a;
        mSize = mFishType.mStartFishSize;
        mDeathTime = mAquarium.mCurrentTime + fishType.mLifetime;
    }


    public void control(PointF direction, String foodName) {
        // мертвой рыбой нельзя управлять
        if (! isAlive()) {
            return;
        }

        mA = (float) atan2(direction.y - mY, direction.x - mX);
        checkWalls(true);
        mX += mFishType.mSpeedRun * cos(mA);
        mY += mFishType.mSpeedRun * sin(mA);
        checkBorders();

        // контролируемая рыба ищет еду среди рыб того типа, имя которого передано
        Fish result = null;
        float distance = Float.MAX_VALUE;
        float d;
        for (Map.Entry<FishType, ArrayList<Fish>> entry : mAquarium.mFishes.entrySet()) {
            if (entry.getKey().getName().equals(foodName)) {
                for (Fish fish : entry.getValue()) {
                        d = distanceTo(fish);
                        if (d < mFishType.mVision && d < distance) {
                            result = fish;
                            distance = d;
                        }
                }
            }
        }
        if (result != null && distance < result.mSize * 3) {
            eat(result);
        }

        mStepDone = true;
    }

    public void step() {
        // мертвая рыба не может ходить
        if (! isAlive()) {
            return;
        }

        // если рыба еще не сделала ход
        // (она уже могла сделать ход, если ею управляет мышка)
        // или что-то еще
        if (!mStepDone) {
            int currentDiplomaticStatus = 0;
            // поиск врагов.
            // получение минимального дипломатического статуса
            for (FishType fishType : mAquarium.mFishes.keySet()) {
                int newDiplomaticStatus = mFishType.getDiplomacyStatus(fishType);
                if (newDiplomaticStatus < currentDiplomaticStatus) {
                    currentDiplomaticStatus = newDiplomaticStatus;
                }
            }
            while (currentDiplomaticStatus < 0) {
                Fish nearestFish = findNearest(currentDiplomaticStatus, false);
                if (nearestFish != null) {
                    run(nearestFish, false); // убегать
                    mStepDone = true;
                    break;
                }
                ++currentDiplomaticStatus;
            }
        }
        // если рыба еще не сделала ход, то она ищет еду
        if (!mStepDone) {
            // получение максимального дипломатического статуса
            int currentDiplomaticStatus = 0;
            for (FishType fishType : mAquarium.mFishes.keySet()) {
                int newDiplomaticStatus = mFishType.getDiplomacyStatus(fishType);
                if (newDiplomaticStatus > currentDiplomaticStatus) {
                    currentDiplomaticStatus = newDiplomaticStatus;
                }
            }
            while (currentDiplomaticStatus > 0) {
                Fish nearestFish = findNearest(currentDiplomaticStatus, true);
                if (nearestFish != null) {
                    run(nearestFish, true); // догонять
                    mStepDone = true;
                    break;
                }
                --currentDiplomaticStatus;
            }
        }
        // если она не нашла ни врагов, ни еды, то она просто гуляет, или колеблется на месте
        if (! mStepDone) {
            if (mFishType.mSettled) {
                stay();
            } else {
                walk();
            }
            mStepDone = true;
        }
    }

    private void stay() {
        int d = mAquarium.mRandom.nextInt(18);
        switch (d) {
            case 1: mX -= 1.9;	break;
            case 2: mY -= 1.9;	break;
            case 3: mX += 1.9;	break;
            case 4: mY += 1.9;	break;
            case 5: mX -= 3.4;	break;
            case 6: mY -= 3.4;	break;
            case 7: mX += 3.4;	break;
            case 8: mY += 3.4;	break;
        }
        checkBorders();
    }

    private void walk() {
        checkWalls(false);
        mX += mFishType.mSpeedWalk * cos(mA);
        mY += mFishType.mSpeedWalk * sin(mA);
        int d = mAquarium.mRandom.nextInt(14);
        switch (d) {
            case 1: mA += 0.07f;	break;
            case 2: mA -= 0.07f;	break;
            case 3: mA += 0.05f;	break;
            case 4: mA -= 0.05f;	break;
            case 5: mA += 0.03f;	break;
            case 6: mA -= 0.03f;	break;
        }
        checkBorders();
    }

    private void run(Fish fish, boolean isChase) {
        mA = (float) atan2(fish.mY - mY, fish.mX - mX);
        if (! isChase) {
            mA += Math.PI;
        }
        if (isChase) {
            fish.mChased = true;
        }
        checkWalls(isChase);
        mX += mFishType.mSpeedRun * cos(mA);
        mY += mFishType.mSpeedRun * sin(mA);
        checkBorders();
        if (isChase && distanceTo(fish) < fish.mSize / 1.2) {
            eat(fish);
        }
    }

    public boolean isAlive() {
        return mAquarium.mCurrentTime < mDeathTime;
    }

    private boolean isChased() {
        return mChased;
    }

    void onAquariumStepFinished() {
        mChased = false;
        mStepDone = false;
    }

    private Fish findNearest(int diplomaticStatus, boolean isChase) {
        Fish result = null;
        float distance = Float.MAX_VALUE;
        float d;

        for (Map.Entry<FishType, ArrayList<Fish>> entry : mAquarium.mFishes.entrySet()) {
            if (mFishType.getDiplomacyStatus(entry.getKey()) == diplomaticStatus) {
                for (Fish fish : entry.getValue()) {
                    if (!isChase || !fish.isChased()) {
                        d = distanceTo(fish);
                        if (d < mFishType.mVision && d < distance) {
                            result = fish;
                            distance = d;
                        }
                    }
                }
            }
        }

        return result;
    }

    private void eat(Fish fish) {
        fish.mDeathTime = mAquarium.mCurrentTime;
        // еда продлевает жизнь и увеличивает размер
        mDeathTime += mFishType.mLifetime / 8;
        if (mSize < mFishType.mMaxFishSize) {
            mSize += 0.4;
        }
    }

    private void checkWalls(boolean isChase) {
        float vis = mFishType.mVision / (isChase ? 7.2f : 1.4f);
        float xm = mAquarium.mXMax;
        float ym = mAquarium.mYMax;
        // координаты единичного вектора с углом a
        float i = (float) cos(mA);
        float j = (float) sin(mA);
        if (mX < vis)		i += ((vis - mX) / vis);
        if (mY < vis)		j += ((vis - mY) / vis);
        if (mX > xm - vis)	i -= ((vis + mX - xm) / vis);
        if (mY > ym - vis)	j -= ((vis + mY - ym) / vis);
        mA = (float) atan2(j, i);
    }

    private void checkBorders() {
        float xm = mAquarium.mXMax;
        float ym = mAquarium.mYMax;
        if (mX < mSize) mX = mSize;
        if (mY < mSize) mY = mSize;
        if (mX > xm - mSize) mX = xm - mSize;
        if (mY > ym - mSize) mY = ym - mSize;
    }

    private float distanceTo(Fish fish) {
        return (float) sqrt(pow(fish.mX - mX, 2) + pow(fish.mY - mY, 2));
    }

    // для внешнего мира
    public PointF getLocation() {
        return new PointF(mX, mY);
    }

    // для внешнего мира
    public float getSize() {
        return mSize;
    }
}