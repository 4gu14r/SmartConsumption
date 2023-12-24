package com.application.smartconsumption.ferramentas;

import com.github.mikephil.charting.data.BarEntry;

public class CustomBarEntry {
    String originalDate;
    BarEntry barEntry;

    public CustomBarEntry(String originalDate, BarEntry barEntry) {
        this.originalDate = originalDate;
        this.barEntry = barEntry;
    }
}
