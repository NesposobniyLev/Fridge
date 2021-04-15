package com.example.fridge;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Lab4 extends Fragment {
    private MapView mapView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.lab4, container, false);
        MapKitFactory.initialize(this.getContext());
        mapView = view.findViewById(R.id.map_view);
        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 13.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0), null);
        new Thread(new Runnable() {
            URL url;
            int number = (int) (Math.random() * ((999999999 - -999999999) + 1)) + -999999999;
            HttpURLConnection connection = null;
            public void run() {
                try{
                    try{
                        url = new URL("http://numbersapi.com/"+ number);
                        connection = (HttpURLConnection) url.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line = br.readLine();
                        TextView t = view.findViewById(R.id.textView);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            t.setText(line);
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mapView.getMap().getMapObjects().addPlacemark(new Point(55.791574, 37.593856),
                ImageProvider.fromAsset(this.getContext(), "beze.jpg"));
        mapView.getMap().getMapObjects().addPlacemark(new Point(55.771974, 37.573556),
                ImageProvider.fromAsset(this.getContext(), "brownie.jpeg"));
        mapView.getMap().getMapObjects().addPlacemark(new Point(55.752974, 37.571156),
                ImageProvider.fromAsset(this.getContext(), "casha_mann.jpg"));
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }
}