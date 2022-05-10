package com.siliddor.developper.lucenepourandroid;


import org.apache.lucene.search.highlight.Encoder;

/**
 * A simplified version of Lucene's SimpleHTMLEncoder.
 *
 * This implementation only escapes three XML entity characters.
 */
public class MinimalHTMLEncoder implements Encoder {
    boolean newlineToBr = false;

    public void setNewlineToBr(boolean newlineToBr) {
        this.newlineToBr = newlineToBr;
    }

    @Override
    public String encodeText(String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(plainText.length());
        for (int index = 0; index < plainText.length(); index++) {
            char ch = plainText.charAt(index);

            switch (ch) {
                case '&':
                    result.append("&amp;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                default:
                    if (newlineToBr && ch == '\n') {
                        result.append("<br>");
                    } else {
                        result.append(ch);
                    }
            }
        }
        return result.toString();
    }
}
