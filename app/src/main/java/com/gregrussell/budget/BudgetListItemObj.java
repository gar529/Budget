package com.gregrussell.budget;

import java.sql.Timestamp;

/**
 * Created by greg on 3/25/2017.
 */

public class BudgetListItemObj {

    int budgetID;
    String budgetName;
    double expenses;
    double spent;
    double earned;

    public BudgetListItemObj() {

    }

    public BudgetListItemObj(int budgetID, String budgetName, double expenses, double spent) {

        this.budgetID = budgetID;
        this.budgetName = budgetName;
        this.expenses = expenses;
        this.spent = spent;


    }

    public BudgetListItemObj(int budgetID, String budgetName, double expenses, double spent, double earned) {

        this.budgetID = budgetID;
        this.budgetName = budgetName;
        this.expenses = expenses;
        this.spent = spent;
        this.earned = earned;


    }


    public int getBudgetID() {
        return this.budgetID;
    }

    public void setBudgetID(int id) {
        this.budgetID = id;
    }

    public String getBudgetName() {
        return this.budgetName;
    }

    public void setBudgetName(String name) {
        this.budgetName = name;
    }

    public double getExpenses() {
        return this.expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getSpent() {
        return this.spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public double getEarned(){
        return this.earned;
    }

    public void setEarned(double earned){
        this.earned = earned;
    }


}
