package com.example.bansach.Activity.Admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;

import com.google.firebase.database.*;

import java.util.*;

public class AdminDashboardActivity extends BaseActivityAdmin {

    TextView txtRevenue, txtOrders, txtUsers, txtBooks;
    BarChart barChart;

    DatabaseReference orderRef, userRef, bookRef;

    Map<Integer, Integer> bookCount = new HashMap<>();

    int totalRevenue = 0;
    int totalOrders = 0;
    int totalUsers = 0;
    int totalBooks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        txtRevenue = findViewById(R.id.txtRevenue);
        txtOrders = findViewById(R.id.txtOrders);
        txtUsers = findViewById(R.id.txtUsers);
        txtBooks = findViewById(R.id.txtBooks);

        barChart = findViewById(R.id.barChart);

        orderRef = FirebaseDatabase.getInstance().getReference("Orders");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        bookRef = FirebaseDatabase.getInstance().getReference("book");

        barChart.post(() -> {
            loadOrders();
            loadUsers();
            loadBooks();
        });
        setupHeader();
    }

    // =====================
    // ORDERS + REVENUE + TOP BOOKS
    // =====================
    private void loadOrders() {

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                totalRevenue = 0;
                totalOrders = 0;
                bookCount.clear();

                for (DataSnapshot order : snapshot.getChildren()) {

                    totalOrders++;

                    // 🔥 FIX totalAmount
                    if (order.child("totalAmount").getValue() != null) {
                        double amount = order.child("totalAmount").getValue(Double.class);
                        totalRevenue += amount;
                    }

                    // 🔥 FIX listItems
                    DataSnapshot itemsSnap = order.child("listItems");

                    if (itemsSnap != null && itemsSnap.exists()) {

                        for (DataSnapshot item : itemsSnap.getChildren()) {

                            Integer bookId = item.child("maSP").getValue(Integer.class);
                            Integer qty = item.child("soLuong").getValue(Integer.class);

                            if (bookId != null && qty != null) {
                                bookCount.put(bookId,
                                        bookCount.getOrDefault(bookId, 0) + qty);
                            }
                        }
                    }
                }
                txtRevenue.setText(String.format("%,dđ", totalRevenue));

                txtOrders.setText(totalOrders + " Orders");

                loadChart();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }

    // =====================
    // USERS
    // =====================
    private void loadUsers() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                totalUsers = (int) snapshot.getChildrenCount();
                txtUsers.setText(totalUsers + " Users");
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // =====================
    // BOOKS
    // =====================
    private void loadBooks() {

        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                totalBooks = (int) snapshot.getChildrenCount();
                txtBooks.setText(totalBooks + " Books");
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // =====================
    // CHART
    // =====================
    private void loadChart() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;

        for (Map.Entry<Integer, Integer> entry : bookCount.entrySet()) {

            entries.add(new BarEntry(index, entry.getValue()));
            labels.add("Book " + entry.getKey());
            index++;
        }

        if (entries.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng bán");
        BarData data = new BarData(dataSet);

        barChart.setData(data);
        barChart.invalidate();
    }
}