package com.example.alex.aquarium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.alex.aquarium.aquarium_entity.Aquarium;
import com.example.alex.aquarium.aquarium_entity.Fish;
import com.example.alex.aquarium.aquarium_entity.FishType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AquariumView extends View {

    private static final String MAIN_HISH_NAME = "mainHish";
    private static final String HISH_NAME = "hish";
    private static final String HISH2_NAME = "hish2";
    private static final String TRAV_NAME = "trav";
    private static final String TRAV2_NAME = "trav2";
    private static final String HORROR_NAME = "horror";

    private Paint mPaint;

    private Aquarium mAquarium;

    // специальный тип рыб, который создается в месте касания пользователя,
    // чтобы остальные рыбы пугались и отплывали.
    private FishType mHorrorType;

    private Aquarium getStartAquarium(float width, float height) {
        Aquarium aquarium = new Aquarium(width, height);

        int fishSize = Utils.dp2px(getResources(), 6);

        FishType mainHish = new FishType(MAIN_HISH_NAME, 5.0f, 7.00f, 110, 500, 1200, 3, false, fishSize, fishSize);
        FishType hish = new FishType(HISH_NAME, 4.4f, 6.94f, 100, 160, 700, 18, false, fishSize, fishSize);
        FishType hish2 = new FishType(HISH2_NAME, 4.4f, 6.92f, 95, 180, 700, 18, false, fishSize, fishSize);
        FishType trav = new FishType(TRAV_NAME, 3.7f, 6.86f, 88, 50, 500, 45, true, fishSize, fishSize);
        FishType trav2 = new FishType(TRAV2_NAME, 3.7f, 6.83f, 84, 60, 500, 45, false, fishSize, fishSize);

        mHorrorType = new FishType(HORROR_NAME, 0.0f, 0.00f, 0, 1000000, 20, 7, true, fishSize, fishSize);

        aquarium.addFishType(trav);
        aquarium.addFishType(trav2);
        aquarium.addFishType(hish);
        aquarium.addFishType(hish2);
        aquarium.addFishType(mainHish);
        aquarium.addFishType(mHorrorType);

        // добавляем самих рыб
        aquarium.addFish(new Fish(aquarium, mainHish, 400, 100, 0));
        aquarium.addFish(new Fish(aquarium, mainHish, 400, 300, 90));
        for (int i = 0; i <= 9; ++i) {
            aquarium.addFish(new Fish(aquarium, hish, 400, 500, i + 1));

            aquarium.addFish(new Fish(aquarium, hish2, 400, 500, i + 4));

            aquarium.addFish(new Fish(aquarium, trav, 40, 200, 0));
            aquarium.addFish(new Fish(aquarium, trav, 50, 200, 0));

            aquarium.addFish(new Fish(aquarium, trav2, 300, 230, 3));
            aquarium.addFish(new Fish(aquarium, trav2, 300, 220, 3));
            aquarium.addFish(new Fish(aquarium, trav2, 300, 230, 3));
        }

        // страшную рыбу все боятся.
        mainHish.setDiplomacyStatus(mHorrorType, -5);
        hish.setDiplomacyStatus(mHorrorType, -5);
        hish2.setDiplomacyStatus(mHorrorType, -5);
        trav.setDiplomacyStatus(mHorrorType, -5);
        trav2.setDiplomacyStatus(mHorrorType, -5);

        // устанавливаем отношения между типами рыб
        mainHish.setDiplomacyStatus(hish, 2);
        mainHish.setDiplomacyStatus(hish2, 1);

        hish.setDiplomacyStatus(trav, 1);
        hish2.setDiplomacyStatus(trav2, 1);

        hish.setDiplomacyStatus(mainHish, -1);
        hish2.setDiplomacyStatus(mainHish, -1);

        trav.setDiplomacyStatus(hish, -1);
        trav2.setDiplomacyStatus(hish2, -1);

        return aquarium;
    }

    public AquariumView(Context context) {
        this(context, null);
    }

    public AquariumView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setKeepScreenOn(true);
        mPaint = new Paint();
        mPaint.setTextSize(Utils.dp2px(getResources(), 14));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        anew();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.aquarium_background));

        if (mAquarium == null) {
            return;
        }

        HashMap<FishType, ArrayList<Fish>> fishes = mAquarium.getFishes();
        for (Map.Entry<FishType, ArrayList<Fish>> entry : fishes.entrySet()) {
            int color;
            switch (entry.getKey().getName()) {
                case MAIN_HISH_NAME:
                    color = getResources().getColor(R.color.mainHish);
                    break;
                case HISH_NAME:
                    color = getResources().getColor(R.color.hish);
                    break;
                case HISH2_NAME:
                    color = getResources().getColor(R.color.hish2);
                    break;
                case TRAV_NAME:
                    color = getResources().getColor(R.color.trav);
                    break;
                case TRAV2_NAME:
                    color = getResources().getColor(R.color.trav2);
                    break;
                case HORROR_NAME:
                    color = getResources().getColor(R.color.horror);
                    break;
                default:
                    Toast.makeText(getContext(), "smth wrong with names!", Toast.LENGTH_SHORT).show();
                    color = 0x00ffff;
                    break;
            }
            mPaint.setColor(color);

            for (Fish fish : entry.getValue()) {
                PointF p = fish.getLocation();
                float sz = fish.getSize();
                canvas.drawRect(p.x - sz / 2, p.y - sz / 2, p.x + sz / 2, p.y + sz / 2, mPaint);
            }
        }

        // вывод текущего времени в аквариуме.
        mPaint.setColor(getResources().getColor(R.color.mainHish));
        canvas.drawText(String.valueOf(mAquarium.getCurrentTime()), 50, mPaint.getTextSize() * 2, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            addHorrorFish(event.getX(), event.getY());
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            getRootView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void addHorrorFish(float x, float y) {
        if (mAquarium != null && mAquarium.getFishes().get(mHorrorType).size() < mHorrorType.getMaxCount()) {
            mAquarium.addFish(new Fish(mAquarium, mHorrorType, x, y, 0));
        }
    }

    public void step() {
        if (mAquarium != null) {
            mAquarium.step();
            invalidate();
        }
    }

    public void anew() {
        createNewAquarium();
        invalidate();
    }

    private void createNewAquarium() {
        float width = getWidth();
        float height = getHeight();
        mAquarium = getStartAquarium(width, height);
    }
}
