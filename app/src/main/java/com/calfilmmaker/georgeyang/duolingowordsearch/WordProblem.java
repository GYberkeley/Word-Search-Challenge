package com.calfilmmaker.georgeyang.duolingowordsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by georgeyang on 2/8/17.
 */

public class WordProblem {
    private static final String JSON_SOURCE_LANG = "source_language";
    private static final String TAG = "WordProblem";
    private static final String JSON_CHAR_GRID = "character_grid";
    private static final String JSON_WORD_COORDS = "word_locations";
    private static final String JSON_WORD = "word";
    private static final String JSON_TARGET_LANG = "target_language";

    private String sourceLanguage;
    private String sourceWord;
    private ArrayList<String> characterGrid = new ArrayList<>();
    private int characterRowSize = 0;
    private String targetLanguage;
    private ArrayList<TargetWord> targetWords = new ArrayList<>();

    public WordProblem(JSONObject jsonObject) {
        try {
            // Parse each JSON Value one at a time

            sourceLanguage = jsonObject.optString(JSON_SOURCE_LANG);
            sourceWord = jsonObject.optString(JSON_WORD);

            JSONArray arrayGrid = jsonObject.optJSONArray(JSON_CHAR_GRID);
            for (int index = 0; index < arrayGrid.length(); index++) {
                JSONArray characterRow = arrayGrid.optJSONArray(index);

                characterRowSize = characterRow.length();
                // Loop through every row and grab its characters
                for (int charIndex = 0; charIndex < characterRowSize; charIndex++) {
                    String singleChar = characterRow.getString(charIndex);
                    characterGrid.add(singleChar);
                }
                Log.d(TAG, "character row: " + characterGrid.toString());
            }

            JSONObject wordLocation = jsonObject.optJSONObject(JSON_WORD_COORDS);
            Log.d(TAG, "sourceWord location: " + wordLocation);
            Iterator<String> keys = wordLocation.keys();
            while (keys.hasNext()) {
                String targetCoords = keys.next();
                ArrayList<Point> points = new ArrayList<>();
                String[] coordsExploded = targetCoords.split(",");
                for (int coordIndex = 0; coordIndex < coordsExploded.length; coordIndex += 2) {
                    String xString = coordsExploded[coordIndex];
                    String yString = coordsExploded[coordIndex + 1];
                    Point point = new Point(Integer.parseInt(xString), Integer.parseInt(yString));
                    Log.d(TAG, "new point: " + point.toString());
                    points.add(point);
                }
                String targetWordString = wordLocation.getString(targetCoords);
                Log.d(TAG, "targetCoords and targetWord: " + targetCoords + "\n" + targetWordString);

                TargetWord targetWord = new TargetWord(targetWordString, points);
                targetWords.add(targetWord);
            }

            targetLanguage = jsonObject.optString(JSON_TARGET_LANG);
        } catch (JSONException jsonException) {
            Log.e(TAG, jsonException.toString());
        }
    }

    /** Getters **/

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getSourceWord() {
        return sourceWord;
    }

    public ArrayList<String> getCharacterGrid() {
        return characterGrid;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public ArrayList<TargetWord> getTargetWords() {
        return targetWords;
    }

    public int getCharacterRowSize() {
        return characterRowSize;
    }

    /** Extra Classes **/

    final class TargetWord implements Comparable {
        String word;
        ArrayList<Point> points = new ArrayList<>();

        public TargetWord(String word, ArrayList<Point> points) {
            this.word = word;
            this.points = points;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TargetWord that = (TargetWord) o;

            if (word != null ? !word.equals(that.word) : that.word != null) return false;
            return points != null ? points.equals(that.points) : that.points == null;

        }

        @Override
        public int hashCode() {
            int result = word != null ? word.hashCode() : 0;
            result = 31 * result + (points != null ? points.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Object o) {
            return equals(o) ? 1 : 0;
        }
    }

    final class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
