package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.ModelImage;
import Model.Order;
import Model.OrderItem;
import Model.ProductCategory;
import Model.ProductModel;
import Model.ProductVariant;

public class StatisticsGraphActivity extends AppCompatActivity {
    private List<ProductModel> modelList = new ArrayList<>();
    private List<OrderItem> orderItemList = new ArrayList<>();
    private List<Integer> yearList = new ArrayList<>();
    private List<Integer> monthList = new ArrayList<>();
    private ArrayAdapter<Integer> yearAdapter, monthAdapter;
    private BarChart chart;
    private boolean isDoneOrderItem = false;
    private TextView tvModelName;
    private Spinner spinnerYear, spinnerMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_graph);

        initView();
        getData();
        setData();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference("OrderItem");
        DatabaseReference orderRef = database.getReference("Order");

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderItemList.clear();
                int totalChildren = (int) dataSnapshot.getChildrenCount();
                final int[] counter = {0}; // Use an array as a workaround

                for (DataSnapshot orderItemSnapshot : dataSnapshot.getChildren()) {
                    OrderItem orderItem = orderItemSnapshot.getValue(OrderItem.class);

                    // Do something with the retrieved OrderItem objects
                    // Retrieve the associated Order ID from OrderItem
                    int orderId = orderItem.getOrderID();

                    // Get the Order reference based on the retrieved Order ID
                    DatabaseReference orderReference = orderRef.child(String.valueOf(orderId));
                    orderReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Order order = dataSnapshot.getValue(Order.class);

                            // Retrieve the date from the Order object
                            String orderDate = order.getDate();

                            if (isRightDate(orderDate)){
                                orderItemList.add(orderItem);
                            }

                            // Increment the counter
                            counter[0]++;

                            // Check if all data has been retrieved
                            if (counter[0] == totalChildren) {
                                isDoneOrderItem = true;
                                // Do something after all data retrieval is completed
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        DatabaseReference ref = database.getReference("Model");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                modelList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductModel model = child1.getValue(ProductModel.class);

                    // get quantity and rate
                    DatabaseReference variantRef = database.getReference("Variant");
                    Query variantQuery = variantRef.orderByChild("modelID").equalTo(model.getID());

                    List<Integer> variantIds = new ArrayList<>();
                    variantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            variantIds.clear();
                            for (DataSnapshot variantSnapshot : dataSnapshot.getChildren()) {
                                ProductVariant variant = variantSnapshot.getValue(ProductVariant.class);
                                variantIds.add(variant.getID());
                            }

                            long quantity = 0;
                            float rate = 0;
                            long count = 0;

                            while (isDoneOrderItem == false);
                            for (OrderItem orderItem : orderItemList){
                                if (variantIds.contains(orderItem.getVariantID())){
                                    quantity += orderItem.getQuantity();
                                    if (orderItem.getRate() != 0){
                                        rate += orderItem.getRate();
                                        ++count;
                                    }
                                }
                            }
                            model.setQuantity(quantity);
                            model.setRate(rate/count);
                            setData();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                    modelList.add(model);
                    setData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setData() {
        ArrayList<String> modelNameList = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < modelList.size(); i++) {
            float quantity = modelList.get(i).getQuantity();
            barEntries.add(new BarEntry(i, quantity));
            modelNameList.add(modelList.get(i).getName());
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
                if (index >= 0 && index < modelNameList.size()) {
                    String modelName = modelNameList.get(index);
                    if (modelName.length() > 8) {
                        return modelName.substring(0, 5) + "...";
                    } else {
                        return modelName;
                    }
                } else {
                    return "";
                }
            }
        });
        xAxis.setLabelRotationAngle(90); // Rotate the labels by 45 degrees
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Place the labels at the bottom of the chart

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setGranularity(1f);

        chart.getAxisRight().setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Model Quantities");
//        barDataSet.setColor(Color.BLUE); // Set your desired color for the bars
        barDataSet.setColor(Color.parseColor("#F44336"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Get the index of the selected bar
                int selectedIndex = (int) e.getX();

                // Retrieve the full label using the selectedIndex
                String fullLabel = modelNameList.get(selectedIndex);

                // Display the full label (e.g., show in a Toast)
               tvModelName.setText(fullLabel);
            }

            @Override
            public void onNothingSelected() {
                // Handle when nothing is selected (optional)
            }
        });

        chart.setData(barData);
        chart.invalidate();

    }

    private void initView() {
        chart = findViewById(R.id.chart);
        tvModelName = findViewById(R.id.tvModelName);
        spinnerYear = findViewById(R.id.spinnerYear);
        for (int i = 2023; i < 2030; ++i){
            yearList.add(i);
        }
        yearAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, yearList);
        yearAdapter.notifyDataSetChanged();
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Call the getData() method here
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected if needed
            }
        });

        spinnerMonth = findViewById(R.id.spinnerMonth);
        for (int i = 1; i < 13; ++i){
            monthList.add(i);
        }
        monthAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, monthList);
        monthAdapter.notifyDataSetChanged();
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean isRightDate(String date){
        int selectYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
        int indexYear = date.lastIndexOf("/");
        int year = Integer.parseInt(date.substring(indexYear + 1));

        int selectMonth = Integer.parseInt(spinnerMonth.getSelectedItem().toString());
        int indexMonth = date.indexOf("/");
        int month = Integer.parseInt(date.substring(indexMonth + 1, indexYear));
        if (selectYear == year && selectMonth == month){
            return true;
        }
        return false;
    }
}