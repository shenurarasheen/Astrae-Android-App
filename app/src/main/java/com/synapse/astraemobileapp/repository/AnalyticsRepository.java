package com.synapse.astraemobileapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AnalyticsRepository {

    private final FirebaseFirestore db;
    private final String currentUserId;

    public AnalyticsRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = FirebaseAuth.getInstance().getUid();
    }

    public void getSellerWeeklyRevenue(WeeklyRevenueCallback callback) {
        // 1. Calculate the time for 7 days ago (Midnight)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long sevenDaysAgo = cal.getTimeInMillis();

        // 2. Fetch all orders from the last 7 days
        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", sevenDaysAgo)
                .get()
                .addOnSuccessListener(orderSnap -> {
                    List<Order> orders = orderSnap.toObjects(Order.class);

                    if (orders.isEmpty()) {
                        callback.onResult(generateEmptyWeekMap());
                        return;
                    }

                    // 3. Collect unique Product IDs to verify who the seller is
                    Set<String> productIds = new HashSet<>();
                    for (Order o : orders) {
                        for (Order.OrderItem item : o.getOrderItems()) {
                            productIds.add(item.getProductId());
                        }
                    }

                    // 4. Fetch Product details to get the sellerId for each product
                    db.collection("products")
                            .whereIn("productId", new ArrayList<>(productIds))
                            .get()
                            .addOnSuccessListener(prodSnap -> {
                                Map<String, String> productSellerMap = new HashMap<>();
                                for (DocumentSnapshot ds : prodSnap.getDocuments()) {
                                    productSellerMap.put(ds.getString("productId"), ds.getString("sellerId"));
                                }

                                // 5. Run the math and filtering
                                Map<String, Double> finalData = processData(orders, productSellerMap);
                                callback.onResult(finalData);
                            });
                })
                .addOnFailureListener(e -> Log.e("Analytics", "Error", e));
    }

    private Map<String, Double> processData(List<Order> orders, Map<String, String> prodSellerMap) {
        Map<String, Double> revenueMap = generateEmptyWeekMap();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);

        for (Order order : orders) {
            String dateKey = sdf.format(new Date(order.getOrderDate()));

            // Ensure the order date falls within our 7-day window
            if (revenueMap.containsKey(dateKey)) {
                double totalForThisSellerInThisOrder = 0;

                for (Order.OrderItem item : order.getOrderItems()) {
                    // FILTER: Get the sellerId for this product
                    String itemSellerId = prodSellerMap.get(item.getProductId());

                    // ONLY if the product belongs to the CURRENT user
                    if (currentUserId != null && currentUserId.equals(itemSellerId)) {
                        totalForThisSellerInThisOrder += (item.getUnitPrice() * item.getQuantity());
                    }
                }

                // Add the filtered total to the specific day
                double currentDayTotal = revenueMap.get(dateKey);
                revenueMap.put(dateKey, currentDayTotal + totalForThisSellerInThisOrder);
            }
        }
        return revenueMap;
    }

    private Map<String, Double> generateEmptyWeekMap() {
        Map<String, Double> map = new LinkedHashMap<>(); // Preserves insertion order
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.US);
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            map.put(sdf.format(cal.getTime()), 0.0);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return map;
    }

    public interface WeeklyRevenueCallback {
        void onResult(Map<String, Double> data);
    }
}
