package com.example.moviebooking.Trailer;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.moviebooking.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class TrailerDialogFragment extends DialogFragment {
    private final String youtubeUrl;

    public TrailerDialogFragment(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trailer, container, false);
        YouTubePlayerView playerView = view.findViewById(R.id.youtubePlayerView);
        getLifecycle().addObserver(playerView);

        String videoId = extractVideoIdFromUrl(youtubeUrl);
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        return view;
    }

    private String extractVideoIdFromUrl(String url) {
        if (url.contains("v=")) {
            return url.substring(url.indexOf("v=") + 2);
        } else if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}