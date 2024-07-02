package fi.benjamin.jpedalboard;

import java.util.ResourceBundle;

public class Res {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("fi/benjamin/jpedalboard/res");

   public static String getString(String value) {
       return bundle.getString(value);
   }
}
