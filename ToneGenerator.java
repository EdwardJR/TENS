package com.example.tens_102;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Toast;

class ToneGenerator {
    Thread t;
    int sr = 44100;
    boolean isRunning = true;

    // burst
    int burst_constant = 10;

    // FM modulation
    double modulation_index = 0.4;
    int carrier_frequency = 10000;

    int flag = 0;
    double fr = 80;



    public void startTone(){
        isRunning = true;
        t = new Thread(){
            public void run(){
                setPriority(Thread.MAX_PRIORITY);

                // creating the buffer
                int buffsize = AudioTrack.getMinBufferSize(sr,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                // create audio object
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, sr,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, buffsize,
                        AudioTrack.MODE_STREAM);

                // set samples
                short samples[] = new short[buffsize];
                short samples_burst[] = new short[burst_constant*buffsize];
                int amp = 10000;
                double twopi = 2.*Math.PI;
                double phi = 0.0;
                double phi_c = 0.0;

                audioTrack.play();

                while (isRunning){
                    // sine wave
                    if (flag == 0){
                        for (int i = 0 ; i < buffsize ; i++){
                            samples[i] = (short) (amp * Math.sin(phi));
                            phi += twopi * fr/sr;
                        }
                        audioTrack.write(samples, 0, buffsize);
                    }
                    // square wave
                    else if (flag == 1){
                        for (int i = 0; i < buffsize; i++) {
                            short s = (short)(amp * Math.sin(phi));
                            if (s > 0.0) {
                                samples[i] = 32767;
                            };

                            if (s < 0.0) {
                                samples[i] = -32767;
                            }
                            phi += twopi * fr / sr;
                        }
                        audioTrack.write(samples, 0, buffsize);
                    }
                    // burst square wave
                    else if (flag == 2){
                        for (int i = 0; i < buffsize; i++) {
                            short s = (short)(amp * Math.sin(phi));
                            if (s > 0.0) {
                                samples_burst[i] = 32767;
                            };

                            if (s < 0.0) {
                                samples_burst[i] = -32767;
                            }
                            phi += twopi * fr / sr;

                        }
                        for (int j = buffsize ; j < burst_constant*buffsize ; j++){
                            samples_burst[j] = 0;
                        }
                        audioTrack.write(samples_burst, 0, burst_constant*buffsize);
                    }
                    // FM modulation
                    else if (flag == 3){
                        for (int i = 0 ; i < buffsize ; i++){
                            samples[i] = (short) (amp * Math.sin(twopi * (phi_c + Math.sin(phi) * modulation_index)));
                            phi_c += twopi*carrier_frequency/sr;
                            phi += twopi * fr/sr;
                        }
                        audioTrack.write(samples, 0, buffsize);
                    }


                }
                audioTrack.stop();
                audioTrack.release();
            }
        };
        t.start();
    }
    public void stopTone(){
        isRunning = false;
        try{
            t.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        t = null;
    }

}

//// sine wave
//                    if (flag == 0){
//                            for (int i = 0 ; i < buffsize ; i++){
//        samples[i] = (short) (amp * Math.sin(phi));
//        phi += twopi * fr/sr;
//        }
//        }
//        // square wave
//        else if (flag == 1){
//        for (int i = 0; i < buffsize; i++) {
//        short s = (short)(amp * Math.sin(phi));
//        if (s > 0.0) {
//        samples[i] = 32767;
//        };
//
//        if (s < 0.0) {
//        samples[i] = -32767;
//        }
//        phi += twopi * fr / sr;
//        }
//        }
//        else if (flag == 2){
//        for (int i = 0; i < buffsize; i++) {
//        short s = (short)(amp * Math.sin(phi));
//        if (s > 0.0) {
//        samples[i] = 32767;
//        };
//
//        if (s < 0.0) {
//        samples[i] = -32767;
//        }
//        phi += twopi * fr / sr;
//        }
//        }
//        else if (flag == 3){
//        for (int i = 0 ; i < buffsize ; i++){
//        samples[i] = (short) (amp * Math.sin(phi));
//        phi += twopi * fr/sr;
//        }
//
//        }
//        audioTrack.write(samples, 0, buffsize);
//        }