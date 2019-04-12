package com.ezz.slotmachine;

import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;


public class SlotMachineAnimator {


    private SlotMachine slotMachine;
    private int mSpeed;
    private int mVelocity;
    private int mFlipDistance;
    private int mDuration;
    private int mSlotCounts;
    private int mTargetPosition;
    private boolean mAnimating = false;
    private boolean mLastAnimation = false;
    private Handler mHandler;
    private int mRollCounts;
    private boolean beginStop = false;
    private SlotMachineStopListener slotMachineStopListener;

    public Runnable mRollNext = new Runnable() {

        @Override
        public void run() {
            roll();
        }

    };

    public SlotMachineAnimator(SlotMachine slotMachine, int slotCounts, int velocity, int flipDistance, SlotMachineStopListener slotMachineStopListener) {
        mHandler = new Handler();
        mVelocity = velocity;
        mFlipDistance = flipDistance;
        this.slotMachine = slotMachine;
        mSlotCounts = slotCounts;
        this.slotMachineStopListener = slotMachineStopListener;
    }

    public void setBeginStop(boolean beginStop) {
        this.beginStop = beginStop;
    }


    /**
     * @param speed        distance per second, normally 1000-10000 will be good
     * @param stopPosition start from 0
     */
    public void start(int speed, int stopPosition) {
        if (mAnimating) {
            return;
        }
        mSpeed = speed;
        mAnimating = true;
        mLastAnimation = false;

        mTargetPosition = stopPosition;
        mRollCounts = 0;
        roll();
    }

    private void roll() {
        calculateSpeedAndDuration();

        animateFlip();
        mRollCounts++;

        if (!mLastAnimation) {
            mHandler.postDelayed(mRollNext, mDuration - 10);
        } else {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    onStop();
                }
            }, mDuration);
        }
    }

    private Interpolator mStartInterpolator = new AnticipateInterpolator();
    private Interpolator mMiddleInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new BounceInterpolator();

    private void animateFlip() {
        Animation in = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, mSpeed < 0 ? 1.0f
                : -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        Interpolator interpolator = mMiddleInterpolator;
        if (mRollCounts == 0) {
            interpolator = mStartInterpolator;
        }
        if (mLastAnimation) {
            interpolator = mEndInterpolator;
        }

        in.setInterpolator(interpolator);
        in.setDuration(mDuration);
        Animation out = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, mSpeed < 0 ? -1.0f : 1.0f);
        out.setInterpolator(interpolator);
        out.setDuration(mDuration);
        slotMachine.clearAnimation();
        slotMachine.setInAnimation(in);
        slotMachine.setOutAnimation(out);
        if (slotMachine.getDisplayedChild() == 0) {
            slotMachine.setDisplayedChild(mSlotCounts - 1);
        } else {
            slotMachine.showPrevious();
        }
    }

    private void calculateSpeedAndDuration() {
        calulateDuration();

        if (shouldStop()) {
            int nextPosition = slotMachine.getDisplayedChild() - 1;
            if (nextPosition < 0) {
                nextPosition = mSlotCounts - 1;
            }
            if (mTargetPosition == nextPosition) {
                stopOnNext();
            } else {
                // keep going
            }
        } else {
            if (beginStop)
                decelerate();
        }
    }

    private void calulateDuration() {
        if (mSpeed != 0) {
            mDuration = Math.abs(mFlipDistance * 1000 / mSpeed);
        } else {
            mDuration = 1000;
        }
        if (mRollCounts == 0) {
            mDuration = 1000;
        }
    }

    private void decelerate() {
        if (mSpeed > 0) {
            mSpeed -= mVelocity;
        } else {
            mSpeed += mVelocity;
        }
    }

    private void stopOnNext() {
        mLastAnimation = true;
        mDuration = 600;
    }

    private boolean shouldStop() {
        if (mRollCounts == 0) {
            return false;
        }
        return mDuration >= 500;
    }

    private void onStop() {
        mAnimating = false;
        if (slotMachineStopListener != null) {
            slotMachineStopListener.onStop(slotMachine.getCurrentView());
        }
    }

}
