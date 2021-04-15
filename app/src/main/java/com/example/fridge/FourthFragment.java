package com.example.fridge;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class FourthFragment extends Fragment {
    public SharedPreferences pref;
    private String currentRecipe = "";
    JSONObject obj = null;

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

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        pref = this.getContext().getSharedPreferences("products", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_four, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentRecipe = pref.getString("current", "");
        TextView text = view.findViewById(R.id.textView6);
        ImageView image = view.findViewById(R.id.imageView);
        TextView text2 = view.findViewById(R.id.textView3);
        text2.setText(currentRecipe);
        try {
            obj = new JSONObject(readJSONFromAsset());
            JSONObject recipe = (JSONObject) obj.get(currentRecipe);
            JSONArray txt = recipe.getJSONArray("text");
            String str = "";
            for (int i = 0; i < txt.length(); i++){
                str += txt.get(i);
            }
            text.setText(str);
            InputStream ims = this.getContext().getAssets().open(recipe.getString("image"));
            Drawable d = Drawable.createFromStream(ims, null);
            ims.close();
            image.setImageDrawable(d);
            image.setMinimumWidth(1000);
            image.setMinimumHeight(1000);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}