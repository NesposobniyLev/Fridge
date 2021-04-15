package com.example.fridge;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecondFragment extends Fragment {
    public SharedPreferences pref;
    private int dynamic_id = 0;
    private List<Button> productList;
    private List<Button> productListAlwaysFull;
    private int clean = -2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        pref = this.getContext().getSharedPreferences("products", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void showList(LinearLayout linearLayout){
        for (int i=0; i < productList.size(); i++) {
            linearLayout.addView(productList.get(i));
        }
    }

    public void addProductToList(View view, String name){
        Button b = new Button(this.getContext());
        b.setId(100000 + dynamic_id);
        dynamic_id++;
        b.setText(name);
        productList.add(b);
        b.setBackgroundColor(getResources().getColor(R.color.red));
        if (pref.getBoolean(name, false)) {
            b.setBackgroundColor(getResources().getColor(R.color.green));
        }
        b.setOnClickListener(v -> {
            Button but = view.findViewById(b.getId());
            ColorDrawable colorDrawable = (ColorDrawable) but.getBackground();
            int colorId = colorDrawable.getColor();
            SharedPreferences.Editor edit = pref.edit();
            if (colorId == getResources().getColor(R.color.red)) {
                but.setBackgroundColor(getResources().getColor(R.color.green));
                edit.putBoolean(name, true);
            } else {
                but.setBackgroundColor(getResources().getColor(R.color.red));
                edit.putBoolean(name, false);
            }
            edit.apply();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        productList = new ArrayList<>();
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open("products.txt")));
            String line = reader.readLine();
            while (line != null) {
                addProductToList(view, line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showList(linearLayout);
        productListAlwaysFull = new ArrayList<>(productList);
        EditText search = view.findViewById(R.id.editText1);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (search.getText().length() == 0){
                    productList.clear();
                    linearLayout.removeAllViews();
                    productList.addAll(productListAlwaysFull);
                    showList(linearLayout);
                } else {
                    productList.clear();
                    linearLayout.removeAllViews();
                    for (Button obj: productListAlwaysFull) {
                        if (obj.getText().length() >= search.getText().length()) {
                            if (Objects.equals(obj.getText().subSequence(0, search.getText().length()).toString().toLowerCase(), search.getText().toString().toLowerCase())) {
                                productList.add(obj);
                            } else {
                                for (int i = 0; i < obj.getText().length(); i++){
                                    char ch = obj.getText().charAt(i);
                                    if (ch == ' ' || ch == '('){
                                        if (search.getText().length()+i+1 <= obj.getText().length()) {
                                            if (Objects.equals(obj.getText().subSequence(i + 1, search.getText().length() + i + 1).toString().toLowerCase(), search.getText().toString().toLowerCase())) {
                                                productList.add(obj);
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    showList(linearLayout);
                }
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.delete);
        fab.setOnClickListener(view1 -> {
            if (clean == -2) {
                Snackbar.make(view1, "Вы полностью очистите свой холодильник!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                clean++;
            } else if (clean == -1) {
                Snackbar.make(view1, "Ещё одно нажатие и готово!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                clean++;
            } else {
                clean = -2;
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.apply();
                NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}