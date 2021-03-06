package com.gregrussell.budget;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by greg on 3/15/2017.
 */

//class that creates an object that holds each item in a row from the Earning table

public class EarningObj {


    int _ID;
    Timestamp _Timestamp;
    Date _Date;
    int _BudgetID;
    String _BudgetName;
    double _Earned;
    String _Description;

    public EarningObj() {

    }

    public EarningObj(int _ID, Timestamp _Timestamp, Date _Date, int _BudgetID, String _BudgetName, double _Earned, String _Description) {

        this._ID = _ID;
        this._Timestamp = _Timestamp;
        this._Date = _Date;
        this._BudgetID = _BudgetID;
        this._BudgetName = _BudgetName;
        this._Earned = _Earned;
        this._Description = _Description;
    }

    public EarningObj(int _ID, Date _Date, int _BudgetID, String _BudgetName, double _Earned, String _Description) {

        this._ID = _ID;
        this._Date = _Date;
        this._BudgetID = _BudgetID;
        this._BudgetName = _BudgetName;
        this._Earned = _Earned;
        this._Description = _Description;
    }

    public EarningObj(Date _Date, int _BudgetID, String _BudgetName, double _Earned, String _Description) {

        this._Date = _Date;
        this._BudgetID = _BudgetID;
        this._BudgetName = _BudgetName;
        this._Earned = _Earned;
        this._Description = _Description;
    }


    public int getID() {
        return this._ID;
    }

    public void setID(int id) {
        this._ID = id;
    }

    public Timestamp getTimestamp() {
        return this._Timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this._Timestamp = timestamp;
    }

    public Date getDate() {
        return this._Date;
    }

    public void setDate(Date date) {
        this._Date = date;
    }

    public int getBudgetID() {
        return this._BudgetID;
    }

    public void setBudgetID(int id) {
        this._BudgetID = id;
    }

    public String getBudgetName() {
        return this._BudgetName;
    }

    public void setBudgetName(String name) {
        this._BudgetName = name;
    }

    public double getEarned() {
        return this._Earned;
    }

    public void setEarned(double earned) {
        this._Earned = earned;
    }

    public String getDescription() {
        return this._Description;
    }

    public void setDescription(String description) {
        this._Description = description;
    }

}
