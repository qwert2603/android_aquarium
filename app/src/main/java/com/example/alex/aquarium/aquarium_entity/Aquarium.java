package com.example.alex.aquarium.aquarium_entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// все поля всех классов этого пакета объявлены как package-local
public class Aquarium implements Serializable {

    float mXMax;
    float mYMax;

    int mCurrentTime = 0;

    HashMap<FishType, ArrayList<Fish>> mFishes = new HashMap<>();

    Random mRandom = new Random();

    public Aquarium(float xm, float ym) {
        mXMax = xm;
        mYMax = ym;
    }

    public void step() {
        // удаляем мертвых рыб
        for(Map.Entry<FishType, ArrayList<Fish>> entry : mFishes.entrySet()) {
            ArrayList<Fish> fishesToDelete = new ArrayList<>();
            for (Fish fish : entry.getValue()) {
                if (! fish.isAlive()) {
                    fishesToDelete.add(fish);
                }
            }
            entry.getValue().removeAll(fishesToDelete);
        }

        // новые рыбы рождаются
        for(Map.Entry<FishType, ArrayList<Fish>> entry : mFishes.entrySet()) {
            FishType fishType = entry.getKey();
            ArrayList<Fish> fishes = entry.getValue();
            int count = fishes.size();
            while (count > 0) {
                --count;
                if (fishes.size() < fishType.mMaxCount && mRandom.nextInt(fishType.mBirthFrequency - 1) == 1) {
                    Fish parent = fishes.get(count);
                    fishes.add(new Fish(this, fishType, parent.mX, parent.mY, parent.mA));
                }
            }
        }

        // рыбы делают ход
        for(Map.Entry<FishType, ArrayList<Fish>> entry : mFishes.entrySet()) {
            for (Fish fish : entry.getValue()) {
                fish.step();
            }
        }

        // действие для каждой рыбы по поводу окончания хода всего аквариума
        for(Map.Entry<FishType, ArrayList<Fish>> entry : mFishes.entrySet()) {
            for (Fish fish : entry.getValue()) {
                fish.onAquariumStepFinished();
            }
        }

        ++mCurrentTime;
    }

    public void addFishType(FishType fishType) {
        mFishes.put(fishType, new ArrayList<Fish>());
    }

    public void addFish(Fish fish) {
        mFishes.get(fish.mFishType).add(fish);
    }

    public HashMap<FishType, ArrayList<Fish>> getFishes() {
        return mFishes;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }
}