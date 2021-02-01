package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class EmbeddedTransactionDAO implements TransactionDAO {
    SQLiteDatabase database;

    public EmbeddedTransactionDAO(SQLiteDatabase db){
        this.database =db;
    }


    @Override
    public void logTransaction(Date date_, String accountNo, ExpenseType expenseType_, double amount_){
        String insertionQuery = "INSERT INTO Account_Transaction (accountNo,expenseType,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(insertionQuery);
        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType_ == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3,amount_);
        statement.bindLong(4,date_.getTime());
        statement.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transaction = new ArrayList<>();
        String Transaction_Selection = "SELECT * FROM Account_Transaction";
        Cursor cursor = database.rawQuery(Transaction_Selection, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Transaction trans=new Transaction(
                            new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                            cursor.getString(cursor.getColumnIndex("accountNo")),
                            (cursor.getInt(cursor.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                            cursor.getDouble(cursor.getColumnIndex("amount")));
                    transaction.add(trans);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> Trans_Detail = new ArrayList<>();
        String Trans_Detail_Selection = "SELECT * FROM Account_Transaction LIMIT"+limit;
        Cursor cursor = database.rawQuery(Trans_Detail_Selection, null);
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction=new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("accountNo")),
                        (cursor.getInt(cursor.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount")));
                Trans_Detail.add(transaction);
            } while (cursor.moveToNext());
        }
        return  Trans_Detail;
    }
}
