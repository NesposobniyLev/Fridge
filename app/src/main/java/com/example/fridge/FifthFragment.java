package com.example.fridge;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FifthFragment extends Fragment {
    public SharedPreferences pref;
    private List<String> recipeList;
    private List<Button> finalList;
    private List<Button> allList;
    JSONObject obj = null;
    private int dynamic_id = 0;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        pref = this.getContext().getSharedPreferences("products", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_five, container, false);
    }

    public void showList(LinearLayout linearLayout){
        for (int i=0; i < allList.size(); i++) {
            linearLayout.addView(allList.get(i));
        }
        for (int i=0; i < finalList.size(); i++) {
            linearLayout.addView(finalList.get(i));
        }
    }

    public String readJSONFromAsset() {
        String json;
        try {
            InputStream is = this.getContext().getAssets().open("recipes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void addProductToList(String name, int count, int accepted){
        Button b = new Button(this.getContext());
        b.setId(300000 + dynamic_id);
        dynamic_id++;
        b.setText(name + " [" + accepted + "/" + count + "]");
        b.setBackgroundColor(getResources().getColor(R.color.white));
        if (count == accepted){
            allList.add(b);
        } else if (count - accepted <= 3) {
            finalList.add(b);
        }
        b.setOnClickListener(v -> {
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("current", name);
            edit.apply();
            NavHostFragment.findNavController(FifthFragment.this).navigate(R.id.action_FifthFragment_to_FourthFragment);
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        recipeList = new ArrayList<>();
        finalList = new ArrayList<>();
        allList = new ArrayList<>();
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout8);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open("recipes.txt")));
            String line = reader.readLine();
            while (line != null) {
                recipeList.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            obj = new JSONObject(readJSONFromAsset());
            for (int i = 0; i < obj.length(); i++) {
                int ingredients_count = 0;
                int accepted_ingredients = 0;
                String currentRecipe = recipeList.get(i);
                JSONObject recipe = (JSONObject) obj.get(currentRecipe);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int j = 0; j < ingredients.length(); j++) {
                    ingredients_count = ingredients.length();
                    String current_ingredient = (String) ingredients.get(j);
                    if (pref.getBoolean(current_ingredient, false) == true) {
                        accepted_ingredients++;
                    }
                }
                addProductToList(currentRecipe, ingredients_count, accepted_ingredients);
            }
            showList(linearLayout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}