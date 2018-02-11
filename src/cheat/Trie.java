package cheat;

public class Trie {
    TrieNode root;
    
    public Trie(String words[]){
        root = new TrieNode();
        for(int i = 0 ;i<words.length;i++){
            addWord(words[i]);
        }
    }
    
    public void addWord(String s){
        TrieNode curr = root;
        for(int i = 0;i<s.length();i++){
            curr = curr.get(s.charAt(i));
        }
        curr.setWord(true);
    }
    
    public boolean contains(String s){
        TrieNode curr = root;
        for(int i = 0;i<s.length();i++){
            if(s.charAt(i) == '*')continue;
            if(!curr.contains(s.charAt(i)))return false;
            curr = curr.get(s.charAt(i));
        }
        return curr.isWord();
    }
}
