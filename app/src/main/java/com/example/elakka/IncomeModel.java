package com.example.elakka;


public class IncomeModel {

    private String DATE;
    private Long INCOME_AMOUNT;
    private String INCOME_TYPE;
    private IncomeModel(){}
    private IncomeModel(String date,Long expense_amount,String expense_type){

        this.DATE = date;
        this.INCOME_AMOUNT = expense_amount;
        this.INCOME_TYPE = expense_type;
    }
    public String getDATE(){return DATE;};
    public Long getINCOME_AMOUNT(){return INCOME_AMOUNT;};
    public String getINCOME_TYPE(){return  INCOME_TYPE;};

}
