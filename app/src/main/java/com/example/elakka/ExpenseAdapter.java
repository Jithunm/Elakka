package com.example.elakka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ExpenseAdapter extends FirestoreRecyclerAdapter<ExpenseModel, ExpenseAdapter.ExpenseHolder> {

    public ExpenseAdapter(@NonNull FirestoreRecyclerOptions<ExpenseModel> options) {
        super(options);
    }

    protected void onBindViewHolder(@NonNull ExpenseHolder holder, int position, @NonNull ExpenseModel model){

        holder.date.setText(model.getDATE());
        holder.amount.setText(String.valueOf(model.getEXPENSE_AMOUNT()));
        holder.type.setText(model.getEXPENSE_TYPE());

}

    public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_item, parent, false);
        return new ExpenseHolder(view);
    }
    public class ExpenseHolder extends RecyclerView.ViewHolder{

    TextView date,type,amount;

    public ExpenseHolder(@NonNull android.view.View itemView){
        super(itemView);
        date = itemView.findViewById(R.id.expense_list_date);
        type = itemView.findViewById(R.id.expense_list_type);
        amount  = itemView.findViewById(R.id.expense_list_amount);


    }


    }

}
