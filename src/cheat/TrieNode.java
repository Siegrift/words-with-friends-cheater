package cheat;

public class TrieNode {
    public static final int ALP_SIZE = 26;
    char ch;
    TrieNode child[] = null;
    TrieNode par;
    boolean cont[] = null;
    boolean isWord=false;

    public TrieNode(TrieNode parent, char ch) {
        par = parent;
        this.ch = ch;
        child = new TrieNode[ALP_SIZE];
        cont = new boolean[ALP_SIZE];
    }
    
    /**Root*/
    public TrieNode() {
        par = null;
        ch = 255;
        child = new TrieNode[ALP_SIZE];
        cont = new boolean[ALP_SIZE];
    }

    public TrieNode get(char ch) {
        int ind = ch - 'A';
        if (!cont[ind]) {
            cont[ind] = true;
            child[ind] = new TrieNode(this,ch);
        }
        return child[ind];
    }

    public void setWord(boolean b) {
        isWord = b;
    }

    public boolean isWord() {
        return isWord;
    }

    public boolean contains(char charAt) {
        return cont[charAt - 'A'];
    }
    
    public String getWord(){
        StringBuilder ans= new StringBuilder();
        TrieNode node = this;
        while(node.par != null){
            ans.append(node.ch);
            node = node.par;
        }
        return ans.reverse().toString();
    }

    // child[] = new TrieNode[26]
}
