package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.fashop.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Order;

public class RevenueActivity extends AppCompatActivity {
    private List<Integer> yearList = new ArrayList<>();
    private List<Integer> monthList = new ArrayList<>();
    private List<String> modeList = new ArrayList<>();
    private ArrayAdapter<Integer> yearAdapter;
    ArrayAdapter<String> modeAdapter;
    boolean isQ = false;
    private BarChart chart;
    private Spinner spinnerYear, spinnerMode;
    Map<Integer, Double> revenueByMonth = new HashMap<>();
    Map<Integer, Double> revenueByQuarter = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renueve);

        chart = findViewById(R.id.chart);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMode = findViewById(R.id.spinnerMode);

        for (int i = 0; i < 13; ++i){
            monthList.add(i);
        }
        for (int i = 2023; i < 2030; ++i){
            yearList.add(i);
        }
        modeList.add("By Month");
        modeList.add("By Quarter");
        yearAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, yearList);
        yearAdapter.notifyDataSetChanged();
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isQ = spinnerMode.getSelectedItem().toString().equals("By Quarter");
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, modeList);
        modeAdapter.notifyDataSetChanged();
        spinnerMode.setAdapter(modeAdapter);
        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isQ = spinnerMode.getSelectedItem().toString().equals("By Quarter");
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getData();
        setData();
    }
    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = database.getReference("Order");

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                revenueByMonth.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    String date = order.getDate();
                    int indexYear = date.lastIndexOf("/");
                    int indexMonth = date.indexOf("/");
                    int month = Integer.parseInt(date.substring(indexMonth + 1, indexYear));
                    int year = Integer.parseInt(date.substring(indexYear + 1));
                    if(order.getStatus().contains("COMPLETED") && year==Integer.parseInt(spinnerYear.getSelectedItem().toString())) {
                        if(revenueByMonth.containsKey(month)) {
                            double totalRevenue = revenueByMonth.get(month) + order.getTotal();
                            revenueByMonth.put(month, totalRevenue);
                        }
                        else {
                            revenueByMonth.put(month, order.getTotal());
                        }
                    }
                }
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void setData() {
        List<BarEntry> yearEntries = new ArrayList<>();
        List<BarEntry> quarterEntries = new ArrayList<>();
        chart.clear();

        if(isQ) {
            for (Map.Entry<Integer, Double> entry : revenueByMonth.entrySet()) {
                int month = entry.getKey();
                double revenue = entry.getValue();
                int quarter = (month - 1) / 3 + 1; // Calculate quarter based on month

                if (revenueByQuarter.containsKey(quarter)) {
                    double totalRevenue = revenueByQuarter.get(quarter) + revenue;
                    revenueByQuarter.put(quarter, totalRevenue);
                } else {
                    revenueByQuarter.put(quarter, revenue);
                }
            }
            for (Map.Entry<Integer, Double> entry : revenueByQuarter.entrySet()) {
                int quarter = entry.getKey();
                float quarterValue = quarter;
                float revenueValue = entry.getValue().floatValue();
                quarterEntries.add(new BarEntry(quarterValue, revenueValue));
            }
        }
        else for (Map.Entry<Integer, Double> entries : revenueByMonth.entrySet()) {
            float a = entries.getKey().floatValue(), b = entries.getValue().floatValue();
            yearEntries.add(new BarEntry(a, b));
        }

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < monthList.size()) {
                    String monthname = monthList.get(index).toString();
                    return monthname;
                } else {
                    return "";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int intValue = (int) value;
                return String.valueOf(intValue);
            }
        });
        yAxis.setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        BarDataSet barDataSet;
        if(isQ)
            barDataSet = new BarDataSet(quarterEntries, "Quarter Revenue ($)");
        else
            barDataSet = new BarDataSet(yearEntries, "Revenue ($)");
//        barDataSet.setColor(Color.BLUE); // Set your desired color for the bars
        barDataSet.setColor(Color.parseColor("#F44336"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Get the index of the selected bar
                int selectedIndex = (int) e.getX();

            }

            @Override
            public void onNothingSelected() {
                // Handle when nothing is selected (optional)
            }
        });

        chart.setData(barData);
        chart.invalidate();
    }

    /*private boolean isRightDate(String date, int selectMonth){
        int selectYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int indexYear = date.lastIndexOf("/");
        int year = Integer.parseInt(date.substring(indexYear + 1));

        int indexMonth = date.indexOf("/");
        int month = Integer.parseInt(date.substring(indexMonth + 1, indexYear));
        if (selectYear == year && selectMonth == month){
            return true;
        }
        return false;
    }*/
    /*private class Revenue{
        long revenuem;
        int month;
        public Revenue(long revenuem, int month) { this.revenuem =revenuem; this.month=month;}
    }*/
}
