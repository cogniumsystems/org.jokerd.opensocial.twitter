/**
 * 
 */
package org.jokerd.opensocial.twitter;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jokerd.opensocial.twitter.model.TweetParser;

/**
 * @author kotelnikov
 */
public class TwitterParserTest extends TestCase {

    /**
     * @param name
     */
    public TwitterParserTest(String name) {
        super(name);
    }

    protected void println(String string) {
        System.out.println(string);
    }

    public void test() throws Exception {
        test("a", "TEXT:'a'");
        test("@a", "USER:'@a'");
        test("#a", "HASH:'#a'");
        test("http://www.foo.bar", "URL:'http://www.foo.bar'");
        test("a #hash b", "TEXT:'a '", "HASH:'#hash'", "TEXT:' b'");
        test("a @user b", "TEXT:'a '", "USER:'@user'", "TEXT:' b'");
        test(
            "before http://www.foo.bar after",
            "TEXT:'before '",
            "URL:'http://www.foo.bar'",
            "TEXT:' after'");
        test(
            "before .http://www.foo.bar. after",
            "TEXT:'before .'",
            "URL:'http://www.foo.bar'",
            "TEXT:'. after'");
        test(
            "before ;http://www.foo.bar; after",
            "TEXT:'before ;'",
            "URL:'http://www.foo.bar'",
            "TEXT:'; after'");
        test(
            "http://www.foo.bar @user #hash",
            "URL:'http://www.foo.bar'",
            "TEXT:' '",
            "USER:'@user'",
            "TEXT:' '",
            "HASH:'#hash'");
    }

    private void test(String str, String... control) {
        final List<String> list = new ArrayList<String>();
        TweetParser parser = new TweetParser();
        println("==========================");
        parser.parse(str, new TweetParser.IListener() {

            private void append(String string) {
                println(string);
                list.add(string);
            }

            public void onHashtag(String value) {
                append("HASH:'" + value + "'");
            }

            public void onText(String value) {
                append("TEXT:'" + value + "'");
            }

            public void onUrl(String value) {
                append("URL:'" + value + "'");
            }

            public void onUserReference(String value) {
                append("USER:'" + value + "'");
            }
        });
        assertEquals(control.length, list.size());
        int i = 0;
        for (String s : control) {
            assertEquals(s, list.get(i++));
        }
    }
}
