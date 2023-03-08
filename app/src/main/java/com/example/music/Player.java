package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Player extends AppCompatActivity {
    TextView titleTV,currentTV,totalTV;
    SeekBar seekBar;
    ImageView pausePlay,previousBtn,musicIcon;
    RelativeLayout nextBtn;
    ArrayList<MusicModel> songsList;
    MusicModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        titleTV = findViewById(R.id.player_songTitle);
        currentTV = findViewById(R.id.player_time_start);
        totalTV = findViewById(R.id.player_time_finish);
        seekBar = findViewById(R.id.player_SeekBar);
        pausePlay = findViewById(R.id.player_button_play);
        previousBtn = findViewById(R.id.player_button_back);
        nextBtn = findViewById(R.id.player_button_next_container);
        musicIcon = findViewById(R.id.player_image);

        songsList = (ArrayList<MusicModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();

        Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTV.setText(convertToMMS(mediaPlayer.getCurrentPosition()+""));
                    if (currentTV.getText().toString().equals(totalTV.getText().toString())){
                        playNextSong();
                    }
                    if (mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.pause);
                        musicIcon.setRotation(x+=2);
                    }else{
                        pausePlay.setImageResource(R.drawable.play);
                        musicIcon.setRotation(x);
                    }
                }

                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTV.setText(currentSong.getTitle());
        totalTV.setText(convertToMMS(currentSong.getDuration()));
        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void playNextSong(){
        MyMediaPlayer.currentIndex++;
        if(MyMediaPlayer.currentIndex == songsList.size()){
            MyMediaPlayer.currentIndex = 0;
        }
        setResourcesWithMusic();
    }
    private void playPreviousSong(){
        MyMediaPlayer.currentIndex--;
        if(MyMediaPlayer.currentIndex < 0){
            MyMediaPlayer.currentIndex = songsList.size() - 1;
        }
        setResourcesWithMusic();
    }
    private void pausePlay(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            pausePlay.setImageResource(R.drawable.play);

        }else{
            mediaPlayer.start();
            pausePlay.setImageResource(R.drawable.pause);

        }
    }
    public static String convertToMMS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}