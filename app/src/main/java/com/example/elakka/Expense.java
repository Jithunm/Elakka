package com.example.elakka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Expense extends AppCompatActivity {
    BottomNavigationView nav;
    private AlertDialog.Builder dialogbuilder;
    private AlertDialog  dialogBox;
    public String type,saved_date;
    public Query query;
    Calendar calendar;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference tblRef = fStore.collection("TBL_EXPENSES");
    private CollectionReference cumulative_expense_tblRef = fStore.collection("TBL_EXPENSE_CUMULATIVE");
    public EditText expenseDate,amount;
    TextView calendar_select;
    ExpenseAdapter adapter;
    ProgressBar pBar;
    int year,month,day,year_;
    Button btnSaveExpense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        saved_date = formatter.format(date);
        setupExpenseRecycler();
        Button new_button = findViewById(R.id.btn_create_new);
        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            CreateNewExpensePopup();
            }
        });
    }

public void setupExpenseRecycler(){
        query = tblRef;
    FirestoreRecyclerOptions<ExpenseModel> options = new FirestoreRecyclerOptions.Builder<ExpenseModel>()
            .setQuery(query,ExpenseModel.class).build();
    adapter =new ExpenseAdapter(options);
    RecyclerView recyclerView = findViewById(R.id.expense_list_recycler);
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
    public void CreateNewExpensePopup(){

        dialogbuilder = new AlertDialog.Builder(this);
        final View new_expensepopup = getLayoutInflater().inflate(R.layout.create_new_popup,null);
        Spinner expense_type = new_expensepopup.findViewById(R.id.spinner_expense_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
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
        dialogbuilder.setView(new_expensepopup);
        dialogBox = dialogbuilder.create();
        dialogBox.show();
        btnSaveExpense = dialogBox.findViewById(R.id.expense_save_button);
        expenseDate = dialogBox.findViewById(R.id.expense_date_picker);
        amount = dialogBox.findViewById(R.id.amount_text);
        calendar_select = dialogBox.findViewById(R.id.calendar_select);

        calendar_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Expense.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        year_ = year;
                        expenseDate.setText(day+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btnSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String  amount_save = amount.getText().toString();
                final int _amount = Integer.parseInt(amount_save);
                final String date_save = expenseDate.getText().toString();

                if(TextUtils.isEmpty(amount_save) || TextUtils.isEmpty(date_save)){
                    Toast.makeText(Expense.this,R.string.validationMessage, Toast.LENGTH_SHORT).show();
                }
                else {

                    String cu_year = String.valueOf(year_);
                    DocumentReference cumulative_expense  = cumulative_expense_tblRef.document(cu_year);
                    cumulative_expense.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists())
                                {

                                    final long amount = document.getLong("TOTAL_EXPENSE").intValue();
                                    long total_income = amount+_amount;

                                    final Map<String, Object> expense_item_update = new HashMap<>();
                                    expense_item_update.put("TOTAL_EXPENSE", total_income);
                                    expense_item_update.put("LAST_UPDATED", saved_date);
                                    cumulative_expense.update(expense_item_update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Expense.this,R.string.totalExpenseUpdateSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Expense.this, R.string.expenseEntryfailure, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    final Map<String, Object> expense_item_new = new HashMap<>();
                                    expense_item_new.put("TOTAL_EXPENSE",_amount);
                                    expense_item_new.put("LAST_UPDATED", saved_date);

                                    cumulative_expense.set(expense_item_new).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Expense.this,R.string.totalExpenseUpdateSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Expense.this,R.string.expenseEntryfailure, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Expense.this,R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });


                    DocumentReference documentReference = fStore.collection("TBL_EXPENSES").document();
                    final Map<String, Object> expense_item = new HashMap<>();

                    expense_item.put("EXPENSE_AMOUNT", _amount);
                    expense_item.put("DATE", date_save);
                    expense_item.put("EXPENSE_TYPE", type);

                    documentReference.set(expense_item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Expense.this, R.string.expenseEntrySuccess, Toast.LENGTH_SHORT).show();
                            dialogBox.hide();
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Expense.this,R.string.expenseEntryfailure, Toast.LENGTH_SHORT).show();
                        }
                    });


                }



            }
        });
    }

}