package com.example.fridge;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
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

public class FirstFragment extends Fragment {
    private List<String> adviceList;
    private int lastAdvice = -1;
    private int lastLastAdvice = -1;
    private int lastLastLastAdvice = -1;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public String getRandomAdvice(){
        int max = adviceList.size()-1;
        int adviceNum = -1;
        do {
            adviceNum = (int) ((Math.random() * ((max) + 1)));
        } while (adviceNum == lastAdvice || adviceNum == lastLastAdvice || adviceNum == lastLastLastAdvice);
        String advice = adviceList.get(adviceNum);
        if (lastAdvice == -1 && lastLastAdvice == -1 && lastLastLastAdvice == -1){
            lastLastLastAdvice = adviceNum;
        } else if (lastAdvice == -1 && lastLastAdvice == -1) {
            lastLastAdvice = lastLastLastAdvice;
            lastLastLastAdvice = adviceNum;
        } else {
            lastAdvice = lastLastAdvice;
            lastLastAdvice = lastLastLastAdvice;
            lastLastLastAdvice = adviceNum;
        }
        return advice;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        FloatingActionButton fab2 = view.findViewById(R.id.fab2);
        adviceList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open("advices.txt")));
            String line = reader.readLine();
            while (line != null) {
                adviceList.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(view1 -> Snackbar.make(view1, getRandomAdvice(), 6000).show());
        fab2.setOnClickListener(view12 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SixthFragment));
        view.findViewById(R.id.button_first).setOnClickListener(view13 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
        view.findViewById(R.id.button_first3).setOnClickListener(view14 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_ThirdFragment));
        view.findViewById(R.id.button_first2).setOnClickListener(view15 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_FifthFragment));
        view.findViewById(R.id.fab3).setOnClickListener(view16 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SeventhFragment));
        view.findViewById(R.id.button).setOnClickListener(view17 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_tolab4));
    }
}