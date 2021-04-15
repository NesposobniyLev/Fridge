package com.example.fridge;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThirdFragment extends Fragment {
    private int dynamic_id = 0;
    private List<Button> recipeList;
    private List<Button> recipeListAlwaysFull;
    public SharedPreferences pref;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        pref = this.getContext().getSharedPreferences("products", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    public void showList(LinearLayout linearLayout){
        for (int i=0; i < recipeList.size(); i++) {
            linearLayout.addView(recipeList.get(i));
        }
    }

    public void addRecipeToList(String name){
        Button b = new Button(this.getContext());
        b.setId(200000 + dynamic_id);
        dynamic_id++;
        b.setText(name);
        recipeList.add(b);
        b.setBackgroundColor(getResources().getColor(R.color.white));
        b.setOnClickListener(v -> {
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("current", name);
            edit.apply();
            NavHostFragment.findNavController(ThirdFragment.this).navigate(R.id.action_ThirdFragment_to_FourthFragment);
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        recipeList = new ArrayList<>();
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout2);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open("recipes.txt")));
            String line = reader.readLine();
            while (line != null) {
                addRecipeToList(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showList(linearLayout);
        recipeListAlwaysFull = new ArrayList<>(recipeList);
        EditText search = view.findViewById(R.id.editText2);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (search.getText().length() == 0){
                    recipeList.clear();
                    linearLayout.removeAllViews();
                    recipeList.addAll(recipeListAlwaysFull);
                    showList(linearLayout);
                } else {
                    recipeList.clear();
                    linearLayout.removeAllViews();
                    for (Button obj: recipeListAlwaysFull) {
                        if (obj.getText().length() >= search.getText().length()) {
                            if (Objects.equals(obj.getText().subSequence(0, search.getText().length()).toString().toLowerCase(), search.getText().toString().toLowerCase())) {
                                recipeList.add(obj);
                            } else {
                                for (int i = 0; i < obj.getText().length(); i++) {
                                    char ch = obj.getText().charAt(i);
                                    if (ch == ' ' || ch == '(') {
                                        if (search.getText().length() + i + 1 <= obj.getText().length()) {
                                            if (Objects.equals(obj.getText().subSequence(i + 1, search.getText().length() + i + 1).toString().toLowerCase(), search.getText().toString().toLowerCase())) {
                                                recipeList.add(obj);
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
    }
}