package com.example.elakka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    String selectedYear;
    TextView incomeTxt,expenseTxt;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private CollectionReference cumulative_income_tblRef = fStore.collection("TBL_INCOME_CUMULATIVE");
    private CollectionReference cumulative_expense_tblRef = fStore.collection("TBL_EXPENSE_CUMULATIVE");
    PieChart pieChart;
    long total_income_amount,total_expense_amount;
    public Long graph_income_amount,graph_expense_amount;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        incomeTxt = findViewById(R.id.income_slice);
        expenseTxt = findViewById(R.id.expense_slice);
        pieChart = findViewById(R.id.piechart);
        toolbar = findViewById( R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drower);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout .addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        setData();
    }

    private void setData()
    {
        Spinner year = findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(adapter);
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = parent.getSelectedItem().toString();
                DocumentReference cumulative_expense  = cumulative_expense_tblRef.document(selectedYear);
                pieChart.clearChart();
                cumulative_expense.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                total_expense_amount = document.getLong("TOTAL_EXPENSE").intValue();
                                expenseTxt.setText(String.valueOf(total_expense_amount));

                            }
                            else
                            {
                                expenseTxt.setText("Nothing to show!");
                                total_expense_amount = 0L;

                            }
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "expense",
                                            total_expense_amount,
                                            Color.parseColor("#FFA726")));


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this,R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
                ////////
                DocumentReference cumulative_income  = cumulative_income_tblRef.document(selectedYear);

                cumulative_income.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                total_income_amount = document.getLong("TOTAL_INCOME").intValue();
                                incomeTxt.setText(String.valueOf(total_income_amount));

                            }
                            else
                            {
                                incomeTxt.setText("Nothing to show!");
                                total_income_amount =0L;
                            }
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "income",
                                            total_income_amount,
                                            Color.parseColor("#66BB6A")));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this,R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });

                pieChart.startAnimation();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.Expense){
            startActivity(new Intent(getApplicationContext(),Expense.class));
        }
        if(item.getItemId() ==R.id.Income){
            startActivity(new Intent(getApplicationContext(),Income.class));
        }
        if(item.getItemId() ==R.id.Settings){
            startActivity(new Intent(this,Settings.class));
        }
        return true;
    }}
