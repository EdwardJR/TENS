package com.example.tens_102;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // countdown timer
    private CountDownTimer timer;

    // widgets
    private Button start_button;
    private Button reset_button;
    private Button resume_button;

    private TextView counter_text;
    public EditText duration_input;

    private Switch sine;
    private Switch burst;
    private Switch square;
    private Switch fm;

    private SeekBar burst_seek;
    private SeekBar freq;

    // variables
    private boolean flag_running;
    private static final long start_time = 600000;
    private long timeleft = start_time;
    private long timeleft_input;

    // tone generator
    private ToneGenerator tone = new ToneGenerator();
    public double frequency = 80;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttons and inputs
        start_button = findViewById(R.id.start_button);
        reset_button = findViewById(R.id.reset_button);
        counter_text = findViewById(R.id.counter_text);
        duration_input = findViewById(R.id.duration_input);
        resume_button = findViewById(R.id.resume_button);

        sine = (Switch) findViewById(R.id.sine_id);
        square = (Switch) findViewById(R.id.square_id);
        burst = (Switch) findViewById(R.id.burst_id);
        fm = (Switch) findViewById(R.id.fm_id);

        burst_seek = (SeekBar) findViewById(R.id.burst_duration_id);
        freq = (SeekBar) findViewById(R.id.fr_id);

        freq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//              Toast.makeText(getApplicationContext(), "" + progress + "", Toast.LENGTH_LONG).show();
                tone.fr = (double) progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        burst_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Toast.makeText(getApplicationContext(), "" + progress + "", Toast.LENGTH_LONG).show();
                tone.burst_constant = (int) progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    tone.flag = 0;
                }
            }
        });

        square.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    tone.flag = 1;
                }
            }
        });

        burst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    tone.flag = 2;
                }
            }
        });

        fm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    tone.flag = 3;
                }
            }
        });



        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = duration_input.getText().toString();
                timeleft_input = Integer.parseInt(input)*1000;
                tone.carrier_frequency = 400;
                tone.startTone();


                if (timeleft == 0 || input == null){
                    Toast.makeText(getApplicationContext(),"Please Enter a Valid Number!", Toast.LENGTH_LONG).show();
                }
                else{
                    if (flag_running){
                        pause_timer();
                    }
                    else{
                        timeleft = timeleft_input;
                        start_timer();
                    }
                }

            }
        });
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_timer();
            }
        });
        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume_counter();
            }
        });
    }
    private void resume_counter(){
        start_timer();
        tone.startTone();

    }
    private void start_timer(){
        timer = new CountDownTimer(timeleft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft = millisUntilFinished;
                update_timer();

            }

            @Override
            public void onFinish() {
                flag_running = false;
                counter_text.setText("00:00");
                start_button.setText("START");
                tone.stopTone();
            }
        }.start();
        flag_running = true;
        start_button.setText("PAUSE");
        resume_button.setVisibility(View.INVISIBLE);
        reset_button.setVisibility(View.INVISIBLE);
    }
    private void pause_timer(){
        tone.stopTone();
        String str1 = String.valueOf(timeleft);
        Toast.makeText(getApplicationContext(),str1,Toast.LENGTH_LONG).show();
        timer.cancel();
        flag_running = false;
        start_button.setText("START");
        reset_button.setVisibility(View.VISIBLE);
        resume_button.setVisibility(View.VISIBLE);

    }
    private void reset_timer(){
        if (tone.isRunning){
            tone.stopTone();
        }
        timeleft = timeleft_input;
        counter_text.setText("00:00");
        reset_button.setVisibility(View.INVISIBLE);
        resume_button.setVisibility(View.INVISIBLE);
        String caption = reset_button.getText().toString();
        Toast.makeText(getApplicationContext(),"You just clicked " + caption,Toast.LENGTH_LONG).show();

    }
    private void update_timer(){
        String timeleft_string;
        int minutes = (int)timeleft / 60000;
        int seconds = (int)timeleft % 60000 / 1000;

        timeleft_string = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        counter_text.setText(timeleft_string);
    }


}