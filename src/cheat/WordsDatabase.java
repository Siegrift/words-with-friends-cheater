package cheat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class WordsDatabase {

    private ArrayList<String> words = new ArrayList<String>();
    private Trie trie;
    private ArrayList<Ranker> answer;
    private ArrayList<Integer> possibleIndexes;
    private String tiles;
    private Cell cells[][];
    private int[] cnt;
    private Ranker rank;

    public WordsDatabase() {
        BufferedReader read = null;
        read = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/res/nouns.txt")));
        while (true) {
            String str = null;
            try {
                str = read.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (str == null) {
                break;
            }
            str = str.toUpperCase();
            words.add(str);
            // System.out.println(str);
        }
        rank = new Ranker();
        trie = new Trie(words.toArray(new String[0]));
        //System.out.println(trie.contains("THIO"));
    }

    public void getWords(String mustContain) {
        possibleIndexes = new ArrayList<Integer>();
        for (int i = 0; i < mustContain.length(); i++) {
            cnt[mustContain.charAt(i)]++;
        }

        for (int i = 0; i < words.size(); i++) {
            int j;
            for (j = 0; j < words.get(i).length(); j++) {
                cnt[words.get(i).charAt(j)]--;
                //System.out.printf("%s %d %d\n", words.get(i), j,j );
                if (cnt[words.get(i).charAt(j)] < 0) {
                    //if(words.get(i).equals("THIO"))System.out.println("TTTTTTTTTTTTTTT: "+words.get(i).charAt(j)+" "+mustContain+" "+tiles);
                    break;
                }
            }
            if (j == words.get(i).length()) {
                possibleIndexes.add(i);
                j--;
            }
            for (; j >= 0; j--) {
                cnt[words.get(i).charAt(j)]++;
            }
        }

        for (int i = 0; i < mustContain.length(); i++) {
            cnt[mustContain.charAt(i)]--;
        }
    }

    public void getStartGameWords(Cell cells[][]) {
        this.cells = cells;
        getWords("");
        for (int i = 0; i < possibleIndexes.size(); i++) {
            int ind = possibleIndexes.get(i);
            placeVertically(words.get(ind), Screen.CELL_COUNT / 2, Screen.CELL_COUNT / 2, 0, false, -1);
        }
    }

    private void placeVertically(String word, int x, int y, int len, boolean rateLett, int index) {
        int score = 0, otherScore = 0, doubleMul = 0, tripleMul = 0;
        int i = 0;
        while (i < word.length()) {
            //System.out.println(word + " " + i);
            if (ScrableCheater.isOk(i + x, y) == false) {
                return;
            }
            int lettscore = rank.getScore(word.charAt(i));

            if (cells[i + x][y].containsLetter()) {
                if (i == index && rateLett) {
                    lettscore *= cells[i+x][y].getLetterMultiply();
                    if (cells[i + x][y].isTripleWord()) {
                        tripleMul++;
                    } else if (cells[i + x][y].isDoubleWord()) {
                        doubleMul++;
                    }
                    

                    String complete = getHorizontalWord(i + x, y, word.charAt(i));
                    //System.out.printf("%s,%s,%d,%d,%b\n", word, complete,x,y, trie.contains(complete));
                    if (complete.length() > 1) {
                        if (trie.contains(complete) == false) {
                            return;
                        } else {
                            int sideWordScore = rank.basicRank(complete) + lettscore - rank.getScore(word.charAt(i));
                            otherScore += sideWordScore * cells[i+x][y].getWordMultiply();
                        }
                    }
                }
                if (word.charAt(i) != cells[i + x][y].getLetter()) {
                    return;
                }
            } else {
                lettscore *= cells[i + x][y].getLetterMultiply();
                if (cells[i + x][y].isTripleWord()) {
                    tripleMul++;
                } else if (cells[i + x][y].isDoubleWord()) {
                    doubleMul++;
                }
                
                
                String complete = getHorizontalWord(i + x, y, word.charAt(i));
                //System.out.printf("%s,%s,%d,%d,%b\n", word, complete,x,y, trie.contains(complete));
                if (complete.length() > 1) {
                    if (trie.contains(complete) == false) {
                        return;
                    } else {
                        int sideWordScore = rank.basicRank(complete) + lettscore - rank.getScore(word.charAt(i));
                        otherScore += sideWordScore * cells[i+x][y].getWordMultiply();
                    }
                }
            }
            score += lettscore;
            i++;
        }
        if (ScrableCheater.isOk(i + x, y) && cells[i + x][y].containsLetter()) {
            return;
        }
        if (ScrableCheater.isOk(x - 1, y) && cells[x - 1][y].containsLetter()) {
            return;
        }

        int tscore = score;
        score = 0;
        if (doubleMul > 0) {
            score = 2 * doubleMul * tscore;
        }
        if (tripleMul > 0) {
            score += 3 * tripleMul * tscore;
        }
        if (score == 0) {
            score = tscore;
        }
        if (word.length() - len == 7) {
            score += 35;
        }
        //System.out.printf("%s\t%d\t%d\t%d\n", word, score, otherScore, doubleMul);
        answer.add(new Ranker(word, score + otherScore, x, y, Ranker.DOWN));
    }

    /**
     * @param rateLett this is true when we are creating a TOUCHING word, this
     * means the letter should be scored.
     */
    public void getVertical(int x, int y, boolean rateLett) {
        String toFind = getVerticalWord(x, y, cells[x][y].getLetter());
        getWords(toFind);

        //System.out.printf("%s, %d, %d\n", toFind, x, y);
        for (int i = 0; i < possibleIndexes.size(); i++) {
            int index = -1, wordIndex = possibleIndexes.get(i);

            //we have used 1 letter so some words are not available anymor
            if (rateLett && canConstruct(words.get(wordIndex),toFind) == false) {
                continue;
            }

            while (true) {
                index = words.get(wordIndex).indexOf(toFind, index + 1);
                if (index == -1 || toFind.length() == words.get(wordIndex).length()) {
                    break;
                }
                // if(words.get(wordIndex).equals("RIPIENO"))System.out.printf("%s,%s,%d\n",
                // toFind,
                // words.get(wordIndex), index);
                placeVertically(words.get(wordIndex), x - index, y, toFind.length(), rateLett, index);
            }

            // System.out.println(i);
        }
    }

    /**
     * @param rateLett this is true when we are creating a TOUCHING word, this
     * means the letter should be scored.
     */
    public void getHorizontal(int x, int y, boolean rateLett) {
        String toFind = getHorizontalWord(x, y, cells[x][y].getLetter());
        getWords(toFind);
        if(x == 8 && y == 7)System.out.println("IS HERE: " +toFind +" " + possibleIndexes.contains(words.indexOf("THIO")));

        for (int i = 0; i < possibleIndexes.size(); i++) {
            int index = -1, wordIndex = possibleIndexes.get(i);
            //we have used 1 letter so some words are not available anymore
            if (rateLett && canConstruct(words.get(wordIndex),toFind) == false) {
                continue;
            }

            while (true) {
                index = words.get(wordIndex).indexOf(toFind, index + 1);
                if (index == -1 || toFind.length() == words.get(wordIndex).length()) {
                    break;
                }
                placeHorizontally(words.get(wordIndex), x, y - index, toFind.length(), rateLett, index);
            }

            // System.out.println(i);
        }
    }

    private void placeHorizontally(String word, int x, int y, int len, boolean rateLett, int index) {
        int score = 0, otherScore = 0, doubleMul = 0, tripleMul = 0;
        int i = 0;
        while (i < word.length()) {
            //System.out.printf("%s %d %c\n", word, i, word.charAt(i));
            if (ScrableCheater.isOk(x, i + y) == false) {
                return;
            }
            int lettscore = rank.getScore(word.charAt(i));
            if (cells[x][i + y].containsLetter()) {
                if (i == index && rateLett) {
                    lettscore *= cells[x][i + y].getLetterMultiply();
                    if (cells[x][i + y].isTripleWord()) {
                        tripleMul++;
                    } else if (cells[x][i + y].isDoubleWord()) {
                        doubleMul++;
                    }
                    String complete = getVerticalWord(x, i + y, word.charAt(i));
                    if (complete.length() > 1) {
                        if (trie.contains(complete) == false) {
                            return;
                        } else {
                            int sideWordScore = rank.basicRank(complete) + lettscore - rank.getScore(word.charAt(i));
                            otherScore += sideWordScore * cells[x][y+i].getWordMultiply();
                        }
                    }
                }
                if (word.charAt(i) != cells[x][i + y].getLetter()) {
                    return;
                }
            } else {
                lettscore *= cells[x][i + y].getLetterMultiply();
                if (cells[x][i + y].isTripleWord()) {
                    tripleMul++;
                }
                if (cells[x][i + y].isDoubleWord()) {
                    doubleMul++;
                }

                String complete = getVerticalWord(x, i + y, word.charAt(i));
                if (complete.length() > 1) {
                    if (trie.contains(complete) == false) {
                        return;
                    } else {
                        int sideWordScore = rank.basicRank(complete) + lettscore - rank.getScore(word.charAt(i));
                            otherScore += sideWordScore * cells[x][i+y].getWordMultiply();
                    }
                }

            }
            score += lettscore;
            i++;
        }
        if (ScrableCheater.isOk(x, i + y) && cells[x][i + y].containsLetter()) {
            return;
        }
        if (ScrableCheater.isOk(x, y - 1) && cells[x][y - 1].containsLetter()) {
            return;
        }

        int tscore = score;
        score = 0;
        if (doubleMul > 0) {
            score = 2 * doubleMul * tscore;
        }
        if (tripleMul > 0) {
            score += 3 * tripleMul * tscore;
        }
        if (score == 0) {
            score = tscore;
        }
        if (word.length() - len == 7) {
            score += 35;
        }
        answer.add(new Ranker(word, score + otherScore, x, y, Ranker.RIGHT));
    }

    public void prepare(Cell[][] c, String tiles) {
        answer = new ArrayList<Ranker>();
        cnt = new int[300];
        for (int i = 0; i < tiles.length(); i++) {
            cnt[tiles.charAt(i)]++;
        }
        this.tiles = tiles;
        cells = c;
    }

    public String getVerticalWord(int x, int y, char ch) {
        StringBuilder up = new StringBuilder(), down = new StringBuilder();
        int ind = 1;
        while (ScrableCheater.isOk(x - ind, y) && cells[x - ind][y].containsLetter()) {
            down.append(cells[x - ind][y].getLetter());
            ind++;
        }
        ind = 1;
        while (ScrableCheater.isOk(x + ind, y) && cells[x + ind][y].containsLetter()) {
            up.append(cells[x + ind][y].getLetter());
            ind++;
        }
        down.reverse();
        String complete = String.format("%s%c%s", down, ch, up);
        return complete;
    }

    public String getHorizontalWord(int x, int y, char ch) {
        StringBuilder rgh = new StringBuilder(), lft = new StringBuilder();
        int ind = 1;
        while (ScrableCheater.isOk(x, y - ind) && cells[x][y - ind].containsLetter()) {
            lft.append(cells[x][y - ind].getLetter());
            ind++;
        }
        ind = 1;
        while (ScrableCheater.isOk(x, y + ind) && cells[x][y + ind].containsLetter()) {
            rgh.append(cells[x][y + ind].getLetter());
            ind++;
        }
        lft.reverse();
        String complete = String.format("%s%c%s", lft, ch, rgh);
        return complete;
    }

    public ArrayList<Ranker> getPossible() {
        return answer;
    }

    public void getVerticalTouch(Cell[][] cells, int x, int y, boolean frst) {
        String letts = "";
        if (frst) {
            String word = getHorizontalWord(x, y + 1, cells[x][y + 1].getLetter().charValue());
            for (int i = 0; i < TrieNode.ALP_SIZE; i++) {
                if (trie.contains(('A' + i) + word)) {
                    letts += 'A' + i;
                }
            }
        } else {

        }
        String curWord = "";

        /*if (st.node.isWord())
         answer.add(new Ranker(curWord, st.getScore(), x, y));
         for (int i = 0; i < TrieNode.ALP_SIZE; i++) {
         if (st.node.cont[i]) {
         String str = getHorizontalWord(x + curWord.length() + 1, y, (char) ('A' + 1));
         System.out.println(curWord + " " + str + " " + curWord.length() + " " + x + " " + y);
         if (trie.contains(str))
         s.push(new StackData(st.node.child[i], st.ownScore + rank.getScore((char) (i + 'A'))
         * cell[x + curWord.length() + 1][y].getLetterMultiply(), st.otherScore + rank.basicRank(str)));
         }
         }
         }*/
    }
    
    public ArrayList<Character> getPossiibleLetters(String s) {
        ArrayList<Character> poss = new ArrayList<Character>();
        StringBuffer ss = new StringBuffer(s);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '*') {
                for (int j = 0; j < tiles.length(); j++) {
                    ss.setCharAt(i, (char) tiles.charAt(j));
                    if (trie.contains(new String(ss))) {
                        poss.add(tiles.charAt(j));
                    }
                }
            }
        }
        return poss;
    }

    public void updateCells(Cell[][] cells, String tiles) {
        this.cells = cells;
        this.tiles = tiles;

        for (int i = 0; i < cnt.length; i++) {
            cnt[i] = 0;
        }
        for (int i = 0; i < tiles.length(); i++) {
            cnt[tiles.charAt(i)]++;
        }
    }

    public boolean canConstruct(String word, String alreadyIn) {
        int c[] = new int[26];
        for (int i = 0; i < word.length(); i++) {
            c[word.charAt(i) - 'A']++;
        }
        for (int i = 0; i < alreadyIn.length(); i++) {
            c[alreadyIn.charAt(i) - 'A']--;
        }
        for (int i = 0; i < c.length; i++) {
            if(cnt['A' + i] < c[i])return false;
        }
        return true;
    }

}
