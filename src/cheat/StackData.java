package cheat;

public class StackData {
    int otherScore,ownScore;
    TrieNode node;
    
    public StackData(){}
    public StackData(TrieNode n,int own, int other){
        ownScore = own;
        otherScore = other;
        node = n;
    }
    
    public int getScore(){
        return ownScore + otherScore;
    }
}
