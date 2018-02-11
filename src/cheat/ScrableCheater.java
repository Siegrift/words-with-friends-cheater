package cheat;
import javax.swing.SwingUtilities;


public class ScrableCheater {
    
    public static boolean isOk(int x, int y){
        return x >= 0 && y >= 0 && x < Screen.CELL_COUNT && y < Screen.CELL_COUNT;
    }
    
    public static void main(String a[]){
        
        //System.out.println(new String("jigsaw").indexOf("jig",0 ));
        final WordsDatabase data = new WordsDatabase();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Screen frame = new Screen(data);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
