package com.corochann.androidtvapptutorial;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import java.util.ArrayList;


public class PlaybackOverlayActivity extends Activity {

    private static final String TAG = PlaybackOverlayActivity.class.getSimpleName();
    public static final String AUTO_PLAY = "auto_play";

    private VideoView mVideoView;
    private ArrayList<Movie> mItems = new ArrayList<Movie>();
    private PlaybackController mPlaybackController;

    private Movie mSelectedMovie;
    private int mCurrentItem;

    public PlaybackController getmPlaybackController() {
        return mPlaybackController;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        /* NOTE: setMediaController (in createMediaSession) must be executed
         * BEFORE inflating Fragment!
         */
        mPlaybackController = new PlaybackController(this);

        mItems = MovieProvider.getMovieItems();
        mSelectedMovie = (Movie) getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        //mSelectedMovie = (Movie) getIntent().getExtras().getSerializable(DetailsActivity.MOVIE);
        mCurrentItem = (int) mSelectedMovie.getId() - 1;
        mPlaybackController.setCurrentItem(mCurrentItem);

        setContentView(R.layout.activity_playback_overlay);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mPlaybackController.setVideoView(mVideoView);
        mPlaybackController.setMovie(mSelectedMovie); // it must after video view setting

        loadViews();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mVideoView.suspend();
        mVideoView.setVideoURI(null);
        mPlaybackController.releaseMediaSession();
    }

    private void loadViews() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);

        mPlaybackController.setVideoPath(mSelectedMovie.getVideoUrl());
    }

    private void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playback_overlay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
            // Try to play behind launcher, but if it fails, stop playback.
            //playPause(false);
        }
        } else {
            requestVisibleBehind(false);
        }
    }

}

