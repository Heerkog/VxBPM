package nl.rug.ds.bpm.editor.utils;

/**
 * Created by Mark on 4/17/2016.
 */
public class StringUtils {
    private static final char NEWLINE = '\n';
    private static final String SPACE_SEPARATOR = " ";
    //if text has \n, \r or \t symbols it's better to split by \s+
    private static final String SPLIT_REGEXP= "\\s+";

    public static String breakLines(String input, int maxLineLength) {
        String[] tokens = input.split(SPLIT_REGEXP);
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i];

            if (lineLen + (SPACE_SEPARATOR + word).length() > maxLineLength) {
                if (i > 0) {
                    output.append(NEWLINE);
                }
                lineLen = 0;
            }
            if (i < tokens.length - 1 && (lineLen + (word + SPACE_SEPARATOR).length() + tokens[i + 1].length() <=
                    maxLineLength)) {
                word += SPACE_SEPARATOR;
            }
            output.append(word);
            lineLen += word.length();
        }
        return output.toString();
    }
}
