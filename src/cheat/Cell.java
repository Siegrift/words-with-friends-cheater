package cheat;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Cell extends JPanel {

    //fck this shit

    private static final long serialVersionUID = 1L;
    //user ui cell colors
    public static Color MOUSE_IN_COLOR = Color.GRAY;
    public static Color MOUSE_OUT_COLOR = new Color(240, 240, 240);
    //dw, tw, dl, tl colors
    public static Color DOUBLE_LETTER_COLOR = Color.BLUE;
    public static Color DOUBLE_WORD_COLOR = Color.RED;
    public static Color TRIPLE_LETTER_COLOR = Color.GREEN;
    public static Color TRIPLE_WORD_COLOR = Color.ORANGE;

    private Character chr = null;
    private Color backColor = MOUSE_OUT_COLOR;
    private int xPos, yPos;
    private boolean canShow;
    private char highlight=0;

    public Cell(int x, int y) {
        super();
        canShow = true;
        xPos = x;
        yPos = y;
        setBorder(new LineBorder(Color.BLACK));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(Screen.SYSTEM_FONT);
        g.setColor(Color.BLACK);
        if (chr != null && canShow) {
            String str = chr.toString();
            FontMetrics fm = g.getFontMetrics();
            g.drawString(str, (getWidth() - fm.stringWidth(str)) / 2,
                    (getHeight() + fm.getHeight() - fm.getDescent()) / 2);

        }
        if(canShow && highlight != 0){
            
                g.setColor(Color.GRAY.darker());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                String str = String.valueOf(highlight);
            

            FontMetrics fm = g.getFontMetrics();
            g.drawString(str, (getWidth() - fm.stringWidth(str)) / 2,
                    (getHeight() + fm.getHeight() - fm.getDescent()) / 2);

        }
    }

    public void setDefaultBackground() {
        setBackground(backColor);
    }

    public int getxPos() {
        return xPos;
    }

    public void setShowable(boolean b) {
        canShow = b;
    }

    public int getyPos() {
        return yPos;
    }

    public void setChar(char keyChar) {
        if (keyChar == 0) {
            chr = null;
            return;
        }
        chr = new Character(Character.toUpperCase(keyChar));
    }

    public void setTripleWord() {
        backColor = TRIPLE_WORD_COLOR;
    }

    public void setDoubleWord() {
        backColor = DOUBLE_WORD_COLOR;
    }

    public void setTripleLetter() {
        backColor = TRIPLE_LETTER_COLOR;
    }

    public void setDoubleLetter() {
        backColor = DOUBLE_LETTER_COLOR;
    }

    public boolean isTripleWord() {
        return backColor == TRIPLE_WORD_COLOR;
    }

    public boolean isDoubleWord() {
        return backColor == DOUBLE_WORD_COLOR;
    }

    public boolean isTripleLetter() {
        return backColor == TRIPLE_LETTER_COLOR;
    }

    public boolean isDoubleLetter() {
        return backColor == DOUBLE_LETTER_COLOR;
    }

    public boolean containsLetter() {
        return chr != null;
    }

    public Character getLetter() {
        return chr;
    }

    public int getLetterMultiply() {
        if (isTripleLetter()) {
            return 3;
        }
        if (isDoubleLetter()) {
            return 2;
        }
        return 1;
    }

    public int getWordMultiply() {
        if (isTripleWord()) {
            return 3;
        }
        if (isDoubleWord()) {
            return 2;
        }
        return 1;
    }

    public void highlight(char charAt) {
        highlight = charAt;
    }
}
