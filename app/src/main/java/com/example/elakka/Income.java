package com.example.elakka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Income extends AppCompatActivity {
    BottomNavigationView nav;
    private AlertDialog.Builder dialogbuilder;
    private AlertDialog  dialogBox;
    public String type;
    public Query query;
    Calendar calendar;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference tblRef = fStore.collection("TBL_INCOME");
    private CollectionReference cumulative_income_tblRef = fStore.collection("TBL_INCOME_CUMULATIVE");

    public EditText incomeDate,amount;
    TextView calendar_select;
    IncomeAdapter adapter;
    ProgressBar pBar;
    public int year,month,day,year_;
    public  String saved_date;
    Button btnSaveIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        setupIncomeRecycler();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        saved_date = formatter.format(date);
        Button new_button = findViewById(R.id.btn_create_new_income);
        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateNewIncomePopup();
            }
        });
    }

    public void setupIncomeRecycler(){
        query = tblRef;
        FirestoreRecyclerOptions<IncomeModel> options = new FirestoreRecyclerOptions.Builder<IncomeModel>()
                .setQuery(query,IncomeModel.class).build();
        adapter =new IncomeAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.income_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void CreateNewIncomePopup(){

        dialogbuilder = new AlertDialog.Builder(this);
        final View new_income_popup = getLayoutInflater().inflate(R.layout.create_new_income_popup,null);
        Spinner expense_type = new_income_popup.findViewById(R.id.spinner_income_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.income_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expense_type.setAdapter(adapter);
        expense_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialogbuilder.setView(new_income_popup);
        dialogBox = dialogbuilder.create();
        dialogBox.show();
        btnSaveIncome = dialogBox.findViewById(R.id.income_save_button);
        incomeDate = dialogBox.findViewById(R.id.income_date_picker);
        amount = dialogBox.findViewById(R.id.amount_income);


        calendar_select = dialogBox.findViewById(R.id.calendar_select);


        calendar_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Income.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        year_ = year;
                        incomeDate.setText(day+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btnSaveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String amount_save = amount.getText().toString();
                final long _amount = Integer.parseInt(amount_save);
                final String date_save = incomeDate.getText().toString();

                if(TextUtils.isEmpty(amount_save) || TextUtils.isEmpty(date_save)){
                    Toast.makeText(Income.this,R.string.validationMessage, Toast.LENGTH_SHORT).show();
                }
                else {
                    String cu_year = String.valueOf(year_);
                    DocumentReference cumulative_income  = cumulative_income_tblRef.document(cu_year);
                    cumulative_income.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {

                                final long amount = document.getLong("TOTAL_INCOME").intValue();
                                long total_income = amount+_amount;

                                final Map<String, Object> income_item_update = new HashMap<>();
                                income_item_update.put("TOTAL_INCOME", total_income);
                                income_item_update.put("LAST_UPDATED", saved_date);
                                cumulative_income.update(income_item_update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Income.this,R.string.totalIncomeUpdateSuccess, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Income.this, R.string.incomeEntryfailure, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                final Map<String, Object> income_item_new = new HashMap<>();
                                income_item_new.put("TOTAL_INCOME",_amount);
                                income_item_new.put("LAST_UPDATED", saved_date);

                                cumulative_income.set(income_item_new).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Income.this,R.string.totalIncomeEntrySuccess, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Income.this,R.string.incomeEntryfailure, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Income.this,R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    DocumentReference documentReference = fStore.collection("TBL_INCOME").document();
                    final Map<String, Object> income_item = new HashMap<>();
                    income_item.put("INCOME_AMOUNT", _amount);
                    income_item.put("DATE", date_save);
                    income_item.put("INCOME_TYPE", type);

                    documentReference.set(income_item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Income.this, R.string.incomeEntrySuccess, Toast.LENGTH_SHORT).show();
                            dialogBox.hide();
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Income.this,R.string.incomeEntryfailure, Toast.LENGTH_SHORT).show();
                        }
                    });


                }



            }
        });
    }



}