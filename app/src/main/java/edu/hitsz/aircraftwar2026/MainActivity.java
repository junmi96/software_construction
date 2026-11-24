package edu.hitsz.aircraftwar2026;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.game.EasyGame;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new EasyGame(this));
    }
}