package com.ezz.slotmachine;

import android.view.View;
import android.view.ViewGroup;

public interface SlotMachineAdapter {
    int getCount();

    View getView(ViewGroup viewGroup, int position);
}
