package com.calfilmmaker.georgeyang.duolingowordsearch;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by georgeyang on 2/8/17.
 */

public class WordSearchViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "WordSearchViewHolder";
    View root;
    TextView letterTextView;

    public WordSearchViewHolder(View root) {
        super(root);
        this.root = root;
        this.letterTextView = (TextView) root.findViewById(R.id.letter_text_view);

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "character touched: " + letterTextView.getText());
                return false;
            }
        });
    }
}
