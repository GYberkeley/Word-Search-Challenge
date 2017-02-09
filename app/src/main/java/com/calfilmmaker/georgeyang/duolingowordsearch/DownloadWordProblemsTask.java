package com.calfilmmaker.georgeyang.duolingowordsearch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by georgeyang on 2/8/17.
 */

public class DownloadWordProblemsTask extends AsyncTask<String, Void, Void> {
    public static final String TAG = "FetchWordsAsync";
    public ArrayList<WordProblem> wordProblems = new ArrayList<>();
    public WordFetchCallback callback;

    public interface WordFetchCallback {
        void onWordFetchResult(ArrayList<WordProblem> wordProblems);
    }

    // Set the callback before executing the task
    public void setCallback(WordFetchCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... links) {
        for (String link : links) {
            try {
                URL urlObject = new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlObject.openConnection();
                httpURLConnection.setRequestMethod("GET");
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                ArrayList<JSONObject> words = new ArrayList<>();
                String line;
                while (true) {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    words.add(new JSONObject(line));
                }
                for (JSONObject word : words) {
                    // crete a Word Problem class for its representation
                    WordProblem problem = new WordProblem(word);
                    wordProblems.add(problem);
                }
                Log.d(TAG, words.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (callback != null) {
            callback.onWordFetchResult(wordProblems);
        }
    }
}
