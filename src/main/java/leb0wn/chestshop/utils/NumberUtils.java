package leb0wn.chestshop.utils;

public class NumberUtils {
    public static Integer tryParseInt(String s) {
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public static boolean isInt(String s){
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
}
