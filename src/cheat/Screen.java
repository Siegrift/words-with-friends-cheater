package cheat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Screen extends JFrame {

    // fck this shit
    private static final long serialVersionUID = 1L;
    // scr sizes
    public static final int SCREEN_WIDTH = 950;
    public static final int SCREEN_HEIGHT = 700;
    // cell sizes
    public static final int CELL_WIDTH = 40;
    public static final int CELL_HEIGHT = 40;
    // cel count
    public static final int CELL_COUNT = 15;
    // font
    public static final Font SYSTEM_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font TABLE_FONT = new Font("Arial", Font.BOLD, 14);
    // graphics containers
    private JPanel contentPane;
    private JPanel cellPanel;
    private JTextField playerCharsTextField;
    // own graphics containers
    private Cell cells[][];
    private JButton okButton;
    private WordsDatabase wordDatabase;
    private String tiles;
    // indexes to words database prepared words
    private ArrayList<Integer> possibleWords;
    private JScrollPane scrollPane;
    private JTable table;
    private String colNames[] = {"Slovo", "Skóre", "X", "Y", "Smer"};
    private int highlightX, highlightY, hightlightDir;
    private String highlightWord;

    /**
     * Create the frame.
     */
    public Screen(WordsDatabase data) {
        wordDatabase = data;
        setTitle("Scrable Cheater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        cellPanel = new JPanel();
        cellPanel.setPreferredSize(new Dimension(CELL_COUNT * CELL_WIDTH, CELL_COUNT * CELL_HEIGHT));
        cellPanel.setSize(new Dimension(CELL_COUNT * CELL_WIDTH, CELL_COUNT * CELL_HEIGHT));
        cellPanel.setMaximumSize(new Dimension(CELL_COUNT * CELL_WIDTH, CELL_COUNT * CELL_HEIGHT));
        cellPanel.setMinimumSize(new Dimension(CELL_COUNT * CELL_WIDTH, CELL_COUNT * CELL_HEIGHT));
        cellPanel.setOpaque(true);
        initCells();
        LoadSave ls = new LoadSave();
        ls.load();
        for (int i = 0; i < ls.cells.length; i++) {
            for (int j = 0; j < ls.cells[0].length; j++) {
                cells[i][j].setChar(ls.cells[i][j]);
            }
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveOnExit(e);
            }
        });

        playerCharsTextField = new JTextField();
        playerCharsTextField.setFont(SYSTEM_FONT);
        playerCharsTextField.setBorder(new LineBorder(Color.BLACK));
        playerCharsTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    okButton.doClick();
                }
            }
        });
        playerCharsTextField.setColumns(10);
        playerCharsTextField.setText(ls.tiles);

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okClicked();
            }
        });

        scrollPane = new JScrollPane();

        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPane
                .createSequentialGroup()
                .addContainerGap()
                .addComponent(cellPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(
                        gl_contentPane
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                gl_contentPane
                                .createSequentialGroup()
                                .addComponent(playerCharsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(okButton, GroupLayout.DEFAULT_SIZE, 76, 76))
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)).addContainerGap()));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPane
                .createSequentialGroup()
                .addGap(16)
                .addGroup(
                        gl_contentPane
                        .createParallelGroup(Alignment.LEADING, false)
                        .addGroup(
                                gl_contentPane
                                .createSequentialGroup()
                                .addGroup(
                                        gl_contentPane
                                        .createParallelGroup(Alignment.TRAILING)
                                        .addComponent(playerCharsTextField, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 26,
                                                GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(scrollPane))
                        .addComponent(cellPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(392)));

        table = new JTable(new DefaultTableModel(colNames, 0));
        table.setFont(TABLE_FONT);
        table.setRowHeight(25);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int ind = table.getSelectedRow();
                    if (table.getRowCount() <= ind || ind == -1) {
                        removeHighlight();
                        return;
                    }
                    removeHighlight();
                    highlightWord = (String) table.getModel().getValueAt(ind, 0);
                    highlightX = ((Character) table.getModel().getValueAt(ind, 2)) - 'A';
                    highlightY = ((Integer) table.getModel().getValueAt(ind, 3));
                    hightlightDir = Ranker.getDirection((String) table.getModel().getValueAt(ind, 4));
                    doHighlight();
                }
            }
        });
        // table.setFont(TABLE_FONT);
        // table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollPane.setViewportView(table);
        contentPane.setLayout(gl_contentPane);
    }

    public void okClicked() {
        removeHighlight();
        table.setModel(new DefaultTableModel(colNames, 0));
        boolean firstRound = true;
        tiles = playerCharsTextField.getText();
        tiles = tiles.toUpperCase();
        wordDatabase.prepare(cells, tiles);

        // wordDatabase.getWords(tiles);
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (cells[i][j].containsLetter()) {
                    if (i > 0 && !cells[i - 1][j].containsLetter()) {
                        wordDatabase.updateCells(cells, tiles);
                        wordDatabase.getVertical(i, j, false);
                        if (i - 1 > 0 && !cells[i - 2][j].containsLetter()) {
                            ArrayList<Character> poss = wordDatabase.getPossiibleLetters("*" + wordDatabase.getVerticalWord(i, j, cells[i][j].getLetter()));
                            //Pridaj este skorre za to jedno pismeno ktore sme pridali a cez ktore teraz ideme
                            for (int k = 0; k < poss.size(); k++) {
                                cells[i - 1][j].setChar(poss.get(k));
                                cells[i - 1][j].setShowable(false);
                                StringBuilder nt = new StringBuilder();
                                boolean done = false;
                                for (int m = 0; m < tiles.length(); m++) {
                                    if (tiles.charAt(m) == poss.get(k) && !done) {
                                        done = true;
                                    } else {
                                        nt.append(tiles.charAt(m));
                                    }
                                }
                                //System.out.printf("%s\t%c\t%s\n", tiles, poss.get(k), nt);
                                wordDatabase.updateCells(cells, nt.toString());
                                wordDatabase.getHorizontal(i - 1, j, true);
                                cells[i - 1][j].setChar((char) 0);
                                cells[i - 1][j].setShowable(true);
                            }
                        }
                    }
                    if (j > 0 && !cells[i][j - 1].containsLetter()) {
                        wordDatabase.updateCells(cells, tiles);
                        wordDatabase.getHorizontal(i, j, false);
                        if (j - 1 > 0 && !cells[i][j - 2].containsLetter()) {
                            ArrayList<Character> poss = wordDatabase.getPossiibleLetters("*" + wordDatabase.getHorizontalWord(i, j, cells[i][j].getLetter()));
                            //Pridaj este skorre za to jedno pismeno ktore sme pridali a cez ktore teraz ideme
                            for (int k = 0; k < poss.size(); k++) {
                                cells[i][j - 1].setChar(poss.get(k));
                                cells[i][j - 1].setShowable(false);
                                StringBuilder nt = new StringBuilder();
                                boolean done = false;
                                for (int m = 0; m < tiles.length(); m++) {
                                    if (tiles.charAt(m) == poss.get(k) && !done) {
                                        done = true;
                                    } else {
                                        nt.append(tiles.charAt(m));
                                    }
                                }
                                // System.out.printf("CCCCC: %c\t%d\t%d\n", poss.get(k), i, j-1);
                                wordDatabase.updateCells(cells, nt.toString());
                                wordDatabase.getVertical(i, j - 1, true);
                                cells[i][j - 1].setChar((char) 0);
                                cells[i][j - 1].setShowable(true);
                            }
                        }
                    }

                    //OTHER SIDE ONLY TOUCHING
                    if (cells[i][j].containsLetter()) {
                        if (i + 1 < Screen.CELL_COUNT && !cells[i + 1][j].containsLetter()) {
                            wordDatabase.updateCells(cells, tiles);
                            wordDatabase.getVertical(i, j, false);
                            if (i + 2 < Screen.CELL_COUNT && !cells[i + 2][j].containsLetter()) {
                                ArrayList<Character> poss = wordDatabase.getPossiibleLetters(wordDatabase.getVerticalWord(i, j, cells[i][j].getLetter()) + "*");
                                //Pridaj este skorre za to jedno pismeno ktore sme pridali a cez ktore teraz ideme
                                for (int k = 0; k < poss.size(); k++) {
                                    cells[i + 1][j].setChar(poss.get(k));
                                    cells[i + 1][j].setShowable(false);
                                    StringBuilder nt = new StringBuilder();
                                    boolean done = false;
                                    for (int m = 0; m < tiles.length(); m++) {
                                        if (tiles.charAt(m) == poss.get(k) && !done) {
                                            done = true;
                                        } else {
                                            nt.append(tiles.charAt(m));
                                        }
                                    }
                                    //System.out.printf("%s\t%c\t%s\n", tiles, poss.get(k), nt);
                                    wordDatabase.updateCells(cells, nt.toString());
                                    wordDatabase.getHorizontal(i + 1, j, true);
                                    cells[i + 1][j].setChar((char) 0);
                                    cells[i + 1][j].setShowable(true);
                                }
                            }
                        }
                    }

                    if (j + 1 < Screen.CELL_COUNT && !cells[i][j + 1].containsLetter()) {
                        wordDatabase.updateCells(cells, tiles);
                        wordDatabase.getHorizontal(i, j, false);
                        if (j + 2 < Screen.CELL_COUNT && !cells[i][j + 2].containsLetter()) {
                            ArrayList<Character> poss = wordDatabase.getPossiibleLetters(wordDatabase.getHorizontalWord(i, j, cells[i][j].getLetter()) + "*");
                            //Pridaj este skorre za to jedno pismeno ktore sme pridali a cez ktore teraz ideme
                            for (int k = 0; k < poss.size(); k++) {
                                cells[i][j + 1].setChar(poss.get(k));
                                cells[i][j + 1].setShowable(false);
                                StringBuilder nt = new StringBuilder();
                                boolean done = false;
                                for (int m = 0; m < tiles.length(); m++) {
                                    if (tiles.charAt(m) == poss.get(k) && !done) {
                                        done = true;
                                    } else {
                                        nt.append(tiles.charAt(m));
                                    }
                                }
                                // System.out.printf("CCCCC: %c\t%d\t%d\n", poss.get(k), i, j-1);
                                wordDatabase.updateCells(cells, nt.toString());
                                wordDatabase.getVertical(i, j + 1, true);
                                cells[i][j + 1].setChar((char) 0);
                                cells[i][j + 1].setShowable(true);
                            }
                        }
                    }

                    firstRound = false;

                    /*
                     * if (i > 0 && j > 0 && !cells[i - 1][j].containsLetter()
                     * && !cells[i-1][j-1].containsLetter())
                     * wordDatabase.getVerticalTouch( cells, i-1, j-1,true);
                     * if(i > 0 && j + 1 < CELL_COUNT &&
                     * !cells[i-1][j].containsLetter() &&
                     * !cells[i-1][j+1].containsLetter
                     * ())wordDatabase.getVerticalTouch(cells, i-1, j+1, false);
                     */
                    /*
                     * if (j > 0 && !cells[i][j - 1].containsLetter())
                     * wordDatabase .getHorizontalTouch(cells, i, j);
                     */
                }

            }
        }
        if (firstRound) {
            wordDatabase.prepare(cells, tiles);
            wordDatabase.getStartGameWords(cells);
        }

        ArrayList<Ranker> list = wordDatabase.getPossible();
        Collections.sort(list);
        ArrayList<Ranker> noDuplicates = new ArrayList<Ranker>();
        if (list.size() > 0) {
            noDuplicates.add(list.get(0));
        }
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).compareTo(list.get(i - 1)) != 0) {
                noDuplicates.add(list.get(i));
            }
            //System.out.printf("%s\t%s\t%b\n", list.get(i), list.get(i-1),list.get(i).compareTo(list.get(i-1)) == 0);
        }

        DefaultTableModel tm = (DefaultTableModel) table.getModel();
        for (int i = 0; i < noDuplicates.size(); i++) {
            tm.addRow(noDuplicates.get(i).getData());
        }
        table.validate();
        table.repaint();
    }

    private void initCells() {
        cellPanel.setLayout(new GridLayout(CELL_COUNT, CELL_COUNT));
        cells = new Cell[CELL_COUNT][CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                cells[i][j] = new Cell(i, j);
                if (isTripleWordCell(i, j)) {
                    cells[i][j].setTripleWord();
                } else if (isDoubleWordCell(i, j)) {
                    cells[i][j].setDoubleWord();
                } else if (isTripleLetterCell(i, j)) {
                    cells[i][j].setTripleLetter();
                } else if (isDoubleLetterCell(i, j)) {
                    cells[i][j].setDoubleLetter();
                }
                cells[i][j].setDefaultBackground();
                cells[i][j].setToolTipText(String.format("%c%d", (char) cells[i][j].getxPos() + 'A', cells[i][j].getyPos()));
                cells[i][j].addMouseListener(mouseListener());
                cells[i][j].addKeyListener(keyboardListener());
                cells[i][j].addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        Cell c = (Cell) e.getSource();
                        c.setBackground(Cell.MOUSE_IN_COLOR);
                        c.repaint();
                    }

                    public void focusLost(FocusEvent e) {
                        Cell c = (Cell) e.getSource();
                        c.setDefaultBackground();
                        c.repaint();
                    }
                });
                cellPanel.add(cells[i][j]);
                cells[i][j].repaint();
            }
        }

        // System.out.println(cells[0][0].getSize());
    }

    private boolean isDoubleLetterCell(int i, int j) {
        for (int k = 0; k < BonusCellPosition.DL_SIZE; k++) {
            if (i == BonusCellPosition.DL_X[k] && j == BonusCellPosition.DL_Y[k]) {
                return true;
            }
        }
        return false;
    }

    private boolean isTripleLetterCell(int i, int j) {
        for (int k = 0; k < BonusCellPosition.TL_SIZE; k++) {
            if (i == BonusCellPosition.TL_X[k] && j == BonusCellPosition.TL_Y[k]) {
                return true;
            }
        }
        return false;
    }

    private boolean isDoubleWordCell(int i, int j) {
        for (int k = 0; k < BonusCellPosition.DW_SIZE; k++) {
            if (i == BonusCellPosition.DW_X[k] && j == BonusCellPosition.DW_Y[k]) {
                return true;
            }
        }
        return false;
    }

    private boolean isTripleWordCell(int i, int j) {
        for (int k = 0; k < BonusCellPosition.TW_SIZE; k++) {
            if (i == BonusCellPosition.TW_X[k] && j == BonusCellPosition.TW_Y[k]) {
                return true;
            }
        }
        return false;
    }

    public void saveOnExit(WindowEvent e) {
        char out[][] = new char[CELL_COUNT][CELL_COUNT];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                Character c = cells[i][j].getLetter();
                if (c != null) {
                    out[i][j] = c.charValue();
                }
            }
        }
        new LoadSave().save(out, playerCharsTextField.getText().toUpperCase());
    }

    private KeyListener keyboardListener() {
        return new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                Cell cell = (Cell) e.getSource();
                char ch = e.getKeyChar();
                if (Character.isAlphabetic(ch) == false) {
                    return;
                }
                cell.setChar(ch);
                cell.repaint();
            }

            public void keyPressed(KeyEvent e) {
                Cell cell = (Cell) e.getSource();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cell.setChar((char) 0);
                    cell.repaint();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (ScrableCheater.isOk(cell.getxPos() - 1, cell.getyPos())) {
                        cells[cell.getxPos() - 1][cell.getyPos()].requestFocusInWindow();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (ScrableCheater.isOk(cell.getxPos() + 1, cell.getyPos())) {
                        cells[cell.getxPos() + 1][cell.getyPos()].requestFocusInWindow();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (ScrableCheater.isOk(cell.getxPos(), cell.getyPos() - 1)) {
                        cells[cell.getxPos()][cell.getyPos() - 1].requestFocusInWindow();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (ScrableCheater.isOk(cell.getxPos(), cell.getyPos() + 1)) {
                        cells[cell.getxPos()][cell.getyPos() + 1].requestFocusInWindow();
                    }
                }

            }
        };
    }

    private MouseListener mouseListener() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Cell cell = (Cell) e.getSource();
                cell.requestFocusInWindow();
            }
        };
    }

    public void doHighlight() {
        //System.out.printf("%s %d %d %d\n", highlightWord,highlightX, highlightY, hightlightDir);
       /* cells[highlightX][highlightY].highlight('X');
        repaint();
        boolean b = true;
        if (b) {
            return;
        }*/

        if (highlightWord != null) {
            for (int i = 0; i < highlightWord.length(); i++) {
                if (hightlightDir == Ranker.RIGHT) {
                    cells[highlightX][highlightY + i].highlight(highlightWord.charAt(i));
                } else {
                    cells[highlightX + i][highlightY].highlight(highlightWord.charAt(i));
                }
            }
        }
        repaint();
    }

    public void removeHighlight() {
        //if we want to cancel previous highlighting
        if (highlightWord == null) {
            return;
        }
        
        for (int i = 0; i < highlightWord.length(); i++) {
            if (hightlightDir == Ranker.RIGHT) {
                cells[highlightX][highlightY + i].highlight((char) 0);
            } else {
                cells[highlightX + i][highlightY].highlight((char) 0);
            }
        }
        repaint();
    }
}
