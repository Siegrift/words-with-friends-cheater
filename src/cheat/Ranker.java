package cheat;

import java.security.acl.Owner;
import java.util.Vector;

public class Ranker implements Comparable<Ranker> {
    private String word;
    private int score;
    private int letterScore[] = { 1, 4, 4, 2, 1, 4, 3, 3, 1, 10, 5, 2, 4, 2, 1, 4, 10, 1, 1, 1, 2,
        5, 4, 8, 3, 10 };
    private int X, Y, dir;
    public static int RIGHT = 1;
    public static int DOWN = 2;

    public Ranker(String str) {
        word = str;
        score = 0;
        for (int i = 0; i < str.length(); i++)
            score += letterScore[str.charAt(i) - 'A'];
    }

    public Ranker() {
    }

    public Ranker(String w, int s, int x, int y, int dr) {
        word = w;
        score = s;

        X = x;
        Y = y;
        dir = dr;
    }

    public int getScore(char ch) {
        return letterScore[ch - 'A'];
    }

    public Object get(int col) {
        if (col == 0) return word;
        if (col == 1) return score;
        if (col == 2) return X;
        if(col == 3)return Y;
        return null;
    }

    public int compareTo(Ranker o) {
        if (score != o.score) return o.score - score;
        int r = word.compareTo(o.word);
        if(r != 0)return r;
        
        if(X != o.X)return X - o.X;
        if(Y != o.Y)return Y - o.Y;
        return dir - o.dir;
    }

    public Object[] getData() {
        Object o[] = { word, score, (char) (X + 'A'), Y, formatDir(dir)};
        return o;
    }

    public int basicRank(String str) {
        score = 0;
        for (int i = 0; i < str.length(); i++)
            score += letterScore[str.charAt(i) - 'A'];
        return score;
    }
    
    public String toString(){
        return String.format("%s,%d,%d,%d,%s", word, score, X,Y,formatDir(dir));
    }
    
    public static String formatDir(int dir){
        if(dir == RIGHT)return "→";
        else if(dir == DOWN)return "↓"; 
        return "Bad dir!";
    }
    
     public static int getDirection(String s) {
        if(s.equals("→"))return RIGHT;
        else if(s.equals("↓"))return DOWN;
        return -1;
    }
    
}
