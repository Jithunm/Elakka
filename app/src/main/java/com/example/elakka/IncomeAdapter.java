package com.example.elakka;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class IncomeAdapter extends FirestoreRecyclerAdapter<IncomeModel, IncomeAdapter.IncomeHolder> {

    public IncomeAdapter(@NonNull FirestoreRecyclerOptions<IncomeModel> options) {
        super(options);
    }

    protected void onBindViewHolder(@NonNull IncomeHolder holder, int position, @NonNull IncomeModel model){

        holder.date.setText(model.getDATE());
        holder.amount.setText(String.valueOf(model.getINCOME_AMOUNT()));
        holder.type.setText(model.getINCOME_TYPE());
    }

    public IncomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_list_item, parent, false);
        return new IncomeHolder(view);
    }
    public class IncomeHolder extends RecyclerView.ViewHolder{

        TextView date,type,amount;

        public IncomeHolder(@NonNull android.view.View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.income_list_date);
            type = itemView.findViewById(R.id.income_list_type);
            amount  = itemView.findViewById(R.id.income_list_amount);


        }


    }

}
