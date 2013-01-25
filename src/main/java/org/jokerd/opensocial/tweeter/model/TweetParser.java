package org.jokerd.opensocial.tweeter.model;

/**
 * @author kotelnikov
 */
public class TweetParser {

    /**
     * @author kotelnikov
     */
    public static class FormatListener implements IListener {

        private StringBuilder fBuf = new StringBuilder();

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof FormatListener)) {
                return false;
            }
            FormatListener o = (FormatListener) obj;
            return fBuf.equals(o.fBuf);
        }

        protected String escape(String value) {
            return value;
        }

        protected String formatHashtagUrl(String value) {
            return escape(value);
        }

        protected String formatUrl(String value) {
            return escape(value);
        }

        protected String formatUserUrl(String value) {
            return escape(value);
        }

        @Override
        public int hashCode() {
            return fBuf.hashCode();
        }

        public void onHashtag(String value) {
            String url = formatHashtagUrl(value);
            fBuf.append("<a href='" + url + "'>" + value + "</a>");
        }

        public void onText(String value) {
            fBuf.append(value);
        }

        public void onUrl(String value) {
            String url = formatUrl(value);
            fBuf.append("<a href='" + url + "'>" + value + "</a>");
        }

        public void onUserReference(String value) {
            String url = formatUserUrl(value);
            fBuf.append("<a href='" + url + "'>" + value + "</a>");
        }

        @Override
        public String toString() {
            return fBuf.toString();
        }
    }

    /**
     * @author kotelnikov
     */
    public interface IListener {

        void onHashtag(String value);

        void onText(String value);

        void onUrl(String value);

        void onUserReference(String value);
    }

    private StringBuilder buf = new StringBuilder();

    private char[][] SEQUENCES = {
        "http".toCharArray(),
        "https".toCharArray(),
        "ftp".toCharArray() };

    protected void clearBuf() {
        buf.delete(0, buf.length());
    }

    private int extractUrl(char[] array, int i) {
        for (; i < array.length; i++) {
            char ch = array[i];
            if (Character.isSpaceChar(ch)) {
                break;
            } else if ((ch == ':' || ch == ',' || ch == ';' || ch == '.')
                && (i >= array.length - 2 || Character
                    .isSpaceChar(array[i + 1]))) {
                break;
            }
            buf.append(ch);
        }
        return i;
    }

    private void flushText(IListener listener) {
        if (buf.length() > 0) {
            listener.onText(buf.toString());
        }
        clearBuf();
    }

    private boolean match(char[] array, int pos, char[] seq) {
        int j;
        for (j = 0; pos < array.length && j < seq.length; j++, pos++) {
            if (Character.toLowerCase(array[pos]) != seq[j]) {
                break;
            }
        }
        boolean result = pos < array.length && j == seq.length;
        return result;
    }

    private boolean match(char[] array, int pos, char[][] sequences) {
        boolean result = false;
        for (int i = 0; !result && i < sequences.length; i++) {
            char[] seq = sequences[i];
            result = match(array, pos, seq);
        }
        return result;
    }

    public void parse(String str, IListener listener) {
        char[] array = str.toCharArray();
        clearBuf();
        for (int i = 0; i < array.length;) {
            char ch = array[i];
            if (ch == '@') {
                // Get user reference
                flushText(listener);
                i = extractUrl(array, i);
                listener.onUserReference(buf.toString());
                clearBuf();
            } else if (ch == '#') {
                // Get hash tag
                flushText(listener);
                i = extractUrl(array, i);
                listener.onHashtag(buf.toString());
                clearBuf();
            } else if (match(array, i, SEQUENCES)) {
                flushText(listener);
                i = extractUrl(array, i);
                listener.onUrl(buf.toString());
                clearBuf();
            } else {
                buf.append(ch);
                i++;
            }
        }
        flushText(listener);
    }
}