package com.calfilmmaker.georgeyang.duolingowordsearch;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by georgeyang on 2/8/17.
 */

public class WordSearchAdapter extends RecyclerView.Adapter<WordSearchViewHolder> {
    public static final String TAG = "WordSearchAdapter";
    private ArrayList<String> characterGrid = new ArrayList<>();
    private ArrayList<WordSearchViewHolder> selectedCharacters = new ArrayList<>();
    private ArrayList<WordProblem.TargetWord> targetWords;
    private ArrayList<WordProblem.TargetWord> finishedTargetWords = new ArrayList<>();

    public WordSearchAdapter(ArrayList<String> characterGrid, ArrayList<WordProblem.TargetWord> targetWords) {
        this.characterGrid = characterGrid;
        this.targetWords = targetWords;
    }

    @Override
    public WordSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // The viewholder will have the letter as well as the Point and whether or not it belongs to a word
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new WordSearchViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final WordSearchViewHolder holder, int position) {
        // Bind the character
        holder.letterTextView.setText(characterGrid.get(position));
        holder.root.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        // the number of rows multipled by the elements in each row
        return characterGrid.size();
    }

    public void setNewProblem(WordProblem problem) {

        //  character grid, target word
        this.targetWords = problem.getTargetWords();
        this.characterGrid = problem.getCharacterGrid();
        clearAllSelectedCharacters();

        notifyDataSetChanged();
    }

    public void hitCharacter(WordSearchViewHolder wordSearchViewHolder) {
        if (!selectedCharacters.contains(wordSearchViewHolder)) {
            selectedCharacters.add(wordSearchViewHolder);
        }
        wordSearchViewHolder.root.setBackgroundColor(ContextCompat.getColor(App.context, R.color.colorAccent));
    }

    public boolean isWordFound() {
        String concatenatedWordFromSelection = "";
        for (WordSearchViewHolder selectedCharacter : selectedCharacters) {
            concatenatedWordFromSelection += selectedCharacter.letterTextView.getText().toString();
        }
        Log.d(TAG, "selection: " + concatenatedWordFromSelection);
        for (WordProblem.TargetWord targetWord : targetWords) {
            String targetString = targetWord.word;
            if (TextUtils.equals(concatenatedWordFromSelection, targetString)) {
                if (!finishedTargetWords.contains(targetWord)) {
                    finishedTargetWords.add(targetWord);
                    selectedCharacters.clear();
                }

                return true;
            }
        }

        return false;
    }

    public boolean hasFoundAllWords() {
        for (WordProblem.TargetWord targetWord : targetWords) {
            if (!finishedTargetWords.contains(targetWord)) {
                return false;
            }
        }
        return true;
    }

    public void clearAllSelectedCharacters() {
        for (WordSearchViewHolder wordSearchViewHolder : selectedCharacters) {
            wordSearchViewHolder.root.setBackgroundColor(Color.WHITE);
        }
        selectedCharacters.clear();
    }
}
