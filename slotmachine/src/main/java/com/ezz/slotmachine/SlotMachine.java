package com.ezz.slotmachine;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class SlotMachine extends ViewFlipper {
    private SlotMachineAdapter slotMachineAdapter;
    private SlotMachineAnimator slotMachineAnimator;

    /**
     *
     * @param context
     */
    public SlotMachine(Context context) {
        super(context);
    }

    public SlotMachine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(SlotMachineAdapter slotMachineAdapter) {
        this.slotMachineAdapter = slotMachineAdapter;
        initViews();
    }

    private void initViews() {
        if (slotMachineAdapter != null) {
            for (int i = 0; i < slotMachineAdapter.getCount(); i++) {
                addView(slotMachineAdapter.getView(this, i));
            }
        }
    }

    public void start(int speed, int velocity, int flipDistance, int slotCounts, int stopPosition, SlotMachineStopListener slotMachineStopListener) {
        slotMachineAnimator = new SlotMachineAnimator(this,
                slotCounts,
                velocity,
                flipDistance,
                slotMachineStopListener);
        slotMachineAnimator.start(speed, stopPosition);
    }

    public void beginStop() {
        slotMachineAnimator.setBeginStop(true);
    }
}
