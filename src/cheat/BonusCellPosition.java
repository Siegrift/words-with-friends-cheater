package cheat;

public class BonusCellPosition {
    // triple word
    public static int TW_SIZE = 8;
    public static int TW_X[] = { 0, 0, 3, 3, 11, 11, 14, 14 };
    public static int TW_Y[] = { 3, 11, 0, 14, 0, 14, 3, 11 };
    //doublw word
    public static int DW_SIZE = 12;
    public static int DW_X[] = {1,1,3,5,5,7,7,9,9,11,13,13};
    public static int DW_Y[] = {5,9,7,1,13,3,11,1,13,7,5,9};
    //triple letter
    public static int TL_SIZE = 16;
    public static int TL_X[] = { 0,0,3,3,5,5,6,6,8,8,9,9,11,11,14,14};
    public static int TL_Y[] = {6,8,3,11,5,9,0,14,0,14,5,9,3,11,6,8};
    // double letter
    public static int DL_SIZE = 24;
    public static int DL_X[] = {1,1,2,2,2,2,4,4,4,4,6,6,8,8,10,10,10,10,12,12,12,12,13,13};
    public static int DL_Y[] = {2,12,1,4,10,13,2,6,8,12, 4,10,4,10,2,6,8,12,1,4,10,13,2,12};
}
