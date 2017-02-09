package com.calfilmmaker.georgeyang.duolingowordsearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements DownloadWordProblemsTask.WordFetchCallback {
    private static final String TAG = "MainActivity";
    private static final int START_INDEX = 0;
    private RecyclerView mRecyclerView;
    private WordSearchAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private TextView mWordTextView;
    private ArrayList<WordProblem> wordProblems;
    private int currentWordProblemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parse the JSON with a GET Request
        parseJsonData();

        // Get references
        mWordTextView = (TextView) findViewById(R.id.word);
        mRecyclerView = (RecyclerView) findViewById(R.id.word_search_recycler);
    }

    public void parseJsonData() {
        DownloadWordProblemsTask downloadWordProblemsTask = new DownloadWordProblemsTask();
        downloadWordProblemsTask.setCallback(this);
        downloadWordProblemsTask.execute(getString(R.string.challenge_json));
    }

    public void showAllWordsFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.show_word_dialog_title);
        builder.setMessage(R.string.show_word_dialog_message);
        builder.setPositiveButton(R.string.next_word, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentWordProblemIndex += 1;
                setNewProblem();
            }
        });
        builder.create().show();
    }

    public void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.show_finish_dialog_title);
        builder.setMessage(R.string.show_finish_dialog_message);
        builder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentWordProblemIndex = START_INDEX;
                setNewProblem();
            }
        });
        builder.create().show();
    }

    public void setNewProblem() {
        WordProblem newProblem = wordProblems.get(currentWordProblemIndex);
        mWordTextView.setText(newProblem.getSourceWord() + ": " + newProblem.getTargetWords().size() + " " + getString(R.string.count));
        mLayoutManager.setSpanCount(newProblem.getCharacterRowSize());
        mAdapter.setNewProblem(newProblem);
    }

    public void setRecyclerView() {
        // Init the Adapter, Layout Manager, and attach them to the RecyclerView
        if (!wordProblems.isEmpty()) {
            WordProblem firstWordProblem = wordProblems.get(START_INDEX);

            mWordTextView.setText(firstWordProblem.getSourceWord() + ": " + firstWordProblem.getTargetWords().size() + " " + getString(R.string.count));

            mAdapter = new WordSearchAdapter(firstWordProblem.getCharacterGrid(), firstWordProblem.getTargetWords());
            mRecyclerView.setAdapter(mAdapter);

            Log.d(TAG, "character row size: " + firstWordProblem.getCharacterRowSize());
            mLayoutManager = new GridLayoutManager(
                    this,
                    firstWordProblem.getCharacterRowSize(),
                    GridLayoutManager.HORIZONTAL,
                    false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getAction();

                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            int x = (int) event.getX();
                            int y = (int) event.getY();

                            Rect rect = new Rect();
                            int children = mRecyclerView.getChildCount();

                            // todo: find a more efficient way to get the child at x, y position
                            // currently the runtime is O(n)
                            for (int index = 0; index < children; index++) {
                                View view = mRecyclerView.getChildAt(index);
                                WordSearchViewHolder viewHolder = (WordSearchViewHolder) mRecyclerView.getChildViewHolder(view);

                                // Sets hit rect
                                view.getHitRect(rect);

                                if (rect.contains(x, y)) {
                                    Log.d(TAG, "Hit! at: " + viewHolder.letterTextView.getText().toString());
                                    mAdapter.hitCharacter(viewHolder);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "ACTION_UP");
                            if (mAdapter.isWordFound()) {
                                if (mAdapter.hasFoundAllWords()) {
                                    if (currentWordProblemIndex >= wordProblems.size() - 1) {
                                        showFinishDialog();
                                    } else {
                                        showAllWordsFoundDialog();
                                    }
                                }
                            } else {
                                mAdapter.clearAllSelectedCharacters();
                            }
                            break;
                    }

                    // Figure out where that x, y point is
                    return false;
                }
            });
        }
    }

    // Callback for getting all the Word Problems out of the JSON File
    @Override
    public void onWordFetchResult(ArrayList<WordProblem> wordProblems) {
        this.wordProblems = wordProblems;
        setRecyclerView();
    }
}
