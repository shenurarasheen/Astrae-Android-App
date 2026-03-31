package com.synapse.astraemobileapp.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.SellerItemsAdapter;
import com.synapse.astraemobileapp.databinding.ActivitySellerProductsBinding;
import com.synapse.astraemobileapp.repository.AnalyticsRepository;
import com.synapse.astraemobileapp.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerProductsActivity extends AppCompatActivity {

    private ActivitySellerProductsBinding binding;

    private FirebaseAuth auth;
    private ProductRepository productRepo;
    private AnalyticsRepository analyticsRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        binding = ActivitySellerProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepo = new ProductRepository(this);
        analyticsRepo = new AnalyticsRepository();

        binding.sellingProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String sellerId = auth.getCurrentUser().getUid();
        productRepo.getAllSellerProducts(sellerId, products -> {
                SellerItemsAdapter adapter = new SellerItemsAdapter(products, this);
                binding.sellingProductsRecyclerView.setAdapter(adapter);
        });

        analyticsRepo.getSellerWeeklyRevenue(dataMap -> {
            for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
                Log.d("Revenue", entry.getKey() + ": " + entry.getValue());
            }

            setupCubicChart(dataMap);
        });

        binding.sellerProductsBackBtn.setOnClickListener(v -> {
            finish();
        });

    }

    public void setupCubicChart(Map<String, Double> dataMap) {
        LineChart lineChart = findViewById(R.id.reportingChart);

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            labels.add(entry.getKey());

            entries.add(new Entry(i, entry.getValue().floatValue()));
            i++;
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;

                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
        });

        LineDataSet dataSet = new LineDataSet(entries, "Weekly Sales (LKR)");

        // --- CUBIC MAGIC START ---
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        // --- CUBIC MAGIC END ---

        dataSet.setColor(Color.parseColor("#00E5FF"));
        dataSet.setCircleColor(Color.parseColor("#00E5FF"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawValues(false);

        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#00E5FF"));
        dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.animateY(1000);
        lineChart.invalidate();

        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        Typeface tf = ResourcesCompat.getFont(this, R.font.montserrat_medium);
        lineChart.getXAxis().setTextSize(9f);
        lineChart.getXAxis().setTypeface(tf);
        lineChart.getLegend().setTypeface(tf);


        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        lineChart.getDescription().setTextColor(Color.WHITE);
        lineChart.getDescription().setText("Astrae Analytics");

        xAxis.setTypeface(tf);
        leftAxis.setTypeface(tf);
        legend.setTypeface(tf);

        lineChart.setNoDataText("Waiting for order data...");
        lineChart.setNoDataTextColor(Color.WHITE);
    }
}