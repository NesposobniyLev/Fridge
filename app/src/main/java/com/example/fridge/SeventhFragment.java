package com.example.fridge;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class SeventhFragment extends Fragment {
    public SharedPreferences pref;
    private int dynamic_id = 1;
    public DBHelper dbHelper;
    private int clean = -2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        dbHelper = new DBHelper(this.getContext());
        pref = this.getContext().getSharedPreferences("db", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_seven, container, false);
    }

    public EditText addNote(View view, LinearLayout linearLayout, int id, String note){
        EditText t = new EditText(view.getContext());
        int identificator = 600000 + id;
        t.setId(identificator);
        if (note.length() > 0){
            t.setText(note);
        } else {
            t.setText("- ");
        }
        t.setTextColor(Color.parseColor("#020000"));
        t.setPadding(15, 30, 15, 30);
        t.setTextSize(15);
        t.setBackgroundColor(Color.parseColor("#FFFFFF"));
        t.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (t.getText().toString().length() <= 0){
                    linearLayout.removeView(view.findViewById(identificator));
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("notes", "num = " + identificator, null);
                    dbHelper.close();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (t.getText().toString().length() > 0){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues args = new ContentValues();
                    args.put("note", t.getText().toString());
                    db.update("notes", args, "num = " + identificator, null);
                    dbHelper.close();
                }
            }
        });
        linearLayout.addView(t);
        return t;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dynamic_id = pref.getInt("dynamic_id", 1);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout9);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("notes", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int numColIndex = c.getColumnIndex("num");
            int noteColIndex = c.getColumnIndex("note");
            do {
                addNote(view, linearLayout, c.getInt(numColIndex)-600000, c.getString(noteColIndex));
            } while (c.moveToNext());
        }
        c.close();
        view.findViewById(R.id.add).setOnClickListener(view1 -> {
            SQLiteDatabase db2 = dbHelper.getWritableDatabase();
            Cursor c2 = db2.query("notes", null, null, null, null, null, null);
            int noteCount = c2.getCount();
            c.close();
            if (noteCount <= 100) {
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                EditText t = addNote(view1, linearLayout, dynamic_id, "");
                ContentValues cv = new ContentValues();
                cv.put("num", 600000 + dynamic_id);
                cv.put("note", t.getText().toString());
                db1.insert("notes", null, cv);
                dbHelper.close();
                dynamic_id++;
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("dynamic_id", dynamic_id);
                edit.apply();
            }
        });
        view.findViewById(R.id.kill).setOnClickListener(v -> {
            if (clean == -2) {
                Snackbar.make(v, "Вы удалите все свои заметки! Для удаления одной - очистите текст в ней.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                clean++;
            } else if (clean == -1) {
                Snackbar.make(v, "Ещё одно нажатие и готово! Назад заметки не вернуть!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                clean++;
            } else {
                Snackbar.make(v, "Поздравляем! Можете начинать сначала!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                clean = -2;
                SQLiteDatabase db12 = dbHelper.getWritableDatabase();
                db12.delete("notes", null, null);
                linearLayout.removeAllViews();
                dbHelper.close();
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("dynamic_id", 1);
                dynamic_id = 1;
                edit.apply();
            }
        });
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "database", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table notes ("
                    + "id integer primary key autoincrement,"
                    + "num integer,"
                    + "note text" + ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}