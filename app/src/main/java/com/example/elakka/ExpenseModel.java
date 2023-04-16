package com.example.elakka;

public class ExpenseModel {

    private String DATE;
    private Long EXPENSE_AMOUNT;
    private String EXPENSE_TYPE;
    private ExpenseModel(){}
    private ExpenseModel(String date,Long expense_amount,String expense_type){

        this.DATE = date;
        this.EXPENSE_AMOUNT = expense_amount;
        this.EXPENSE_TYPE = expense_type;
    }
    public String getDATE(){return DATE;};
    public Long getEXPENSE_AMOUNT(){return EXPENSE_AMOUNT;};
    public String getEXPENSE_TYPE(){return  EXPENSE_TYPE;};
}
