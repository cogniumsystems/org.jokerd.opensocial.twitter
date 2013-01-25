package org.jokerd.opensocial.tweeter.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.ubimix.commons.json.JsonObject;

/**
 * @author kotelnikov
 */
/**
 * <pre>
    {
        "is_translator":false,
        "contributors_enabled":false,
        "lang":"en",
        "profile_background_image_url":"http://a0.twimg.com/images/themes/theme1/bg.png",
        "protected":false,
        "profile_image_url_https":"https://si0.twimg.com/profile_images/1335986980/SL_ID_normal.jpg",
        "favourites_count":66,
        "profile_link_color":"c99300",
        "url":"http://fr.linkedin.com/in/sebastienl",
        "name":"Sébastien Lefebvre",
        "default_profile":false,
        "utc_offset":3600,
        "profile_background_color":"69684c",
        "followers_count":462,
        "profile_image_url":"http://a2.twimg.com/profile_images/1335986980/SL_ID_normal.jpg",
        "description":"Founder and CEO @Mesagraph, Tech Entrepreneur\r\n",
        "profile_background_tile":false,
        "listed_count":29,
        "following":true,
        "created_at":"Fri Jul 10 07:54:14 +0000 2009",
        "profile_sidebar_fill_color":"cfbebc",
        "screen_name":"S_Lefebvre",
        "status":{
          "in_reply_to_user_id":1153581,
          "favorited":false,
          "place":null,
          "coordinates":null,
          "retweet_count":0,
          "in_reply_to_screen_name":"terraces",
          "in_reply_to_status_id_str":"156705093708820480",
          "geo":null,
          "retweeted":false,
          "in_reply_to_user_id_str":"1153581",
          "created_at":"Tue Jan 10 14:58:22 +0000 2012",
          "in_reply_to_status_id":156705093708820480,
          "id_str":"156751742539796481",
          "contributors":null,
          "source":"<a href=\"http://twitter.com/tweetbutton\" rel=\"nofollow\">Tweet Button</a>",
          "truncated":false,
          "id":156751742539796481,
          "text":"@terraces biensûr ! ton heure sera la mienne."
        },
        "geo_enabled":true,
        "statuses_count":882,
        "profile_sidebar_border_color":"F2E195",
        "id_str":"55504061",
        "show_all_inline_media":false,
        "follow_request_sent":false,
        "default_profile_image":false,
        "notifications":true,
        "profile_use_background_image":false,
        "friends_count":844,
        "id":55504061,
        "verified":false,
        "profile_background_image_url_https":"https://si0.twimg.com/images/themes/theme1/bg.png",
        "time_zone":"Paris",
        "profile_text_color":"0C3E53",
        "location":"Paris"
    }
 * </pre>
 */
public class Tweet extends JsonObject {

    /**
     * <pre>
        "user":{
            "id":15383497,
            "is_translator":false,
            "profile_background_image_url":"http://a0.twimg.com/profile_background_images/49438641/new-twitter-bkg.png",
            "profile_background_image_url_https":"https://si0.twimg.com/profile_background_images/49438641/new-twitter-bkg.png",
            "friends_count":13045,
            "profile_link_color":"0084B4",
            "default_profile_image":false,
            "utc_offset":-18000,
            "favourites_count":413,
            "name":"The Springpad Team",
            "profile_use_background_image":false,
            "id_str":"15383497",
            "profile_text_color":"333333",
            "protected":false,
            "verified":false,
            "lang":"en",
            "statuses_count":11505,
            "profile_sidebar_border_color":"C0DEED",
            "contributors_enabled":false,
            "url":"http://springpad.com",
            "time_zone":"Eastern Time (US & Canada)",
            "created_at":"Thu Jul 10 21:23:57 +0000 2008",
            "description":"Share and discover with the people you trust. Need help, ideas or inspiration? Get in touch!  ",
            "geo_enabled":false,
            "default_profile":false,
            "notifications":false,
            "profile_background_tile":false,
            "show_all_inline_media":true,
            "profile_image_url_https":"https://si0.twimg.com/profile_images/468311588/icon_springpad_yellow_normal.png",
            "profile_sidebar_fill_color":"DDEEF6",
            "follow_request_sent":false,
            "profile_image_url":"http://a0.twimg.com/profile_images/468311588/icon_springpad_yellow_normal.png",
            "following":true,
            "followers_count":15907,
            "screen_name":"springpad",
            "location":"Boston, MA",
            "listed_count":1501,
            "profile_background_color":"edcb42"
          } 
      </pre>
     * 
     * @author kotelnikov
     */
    public static class TweetUser extends JsonObject {

        public static IJsonValueFactory<TweetUser> FACTORY = new IJsonValueFactory<TweetUser>() {
            @Override
            public TweetUser newValue(Object object) {
                return new TweetUser().setJsonObject(object);
            }
        };

        public TweetUser() {
        }

        public String getDescription() {
            return getString("description");
        }

        public String getDisplayName() {
            return getString("name");
        }

        public String getId() {
            return "" + getValue("id");
        }

        public String getImage() {
            String url = getImageHttp();
            if (url == null) {
                url = getImageHttps();
            }
            return url;
        }

        public String getImageHttp() {
            return getString("profile_image_url");
        }

        public String getImageHttps() {
            return getString("profile_image_url_https");
        }

        public String getLocation() {
            return getString("location");
        }

        public String getName() {
            return getString("name");
        }

        public Date getProfileCreationTime() {
            return getTwitterDate(this, "created_at");
        }

        public String getScreenName() {
            return getString("screen_name");
        }

        public String getUrl() {
            return getString("url");
        }

        public int getUtcOffset() {
            return getInteger("utc_offset", 0);
        }

    }

    public static IJsonValueFactory<Tweet> FACTORY = new IJsonValueFactory<Tweet>() {
        @Override
        public Tweet newValue(Object object) {
            return new Tweet().setJsonObject(object);
        }
    };

    // Tue Apr 08 19:58:28 +0000 2008
    private static SimpleDateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat(
        "EEE MMM dd HH:mm:ss Z yyyy",
        Locale.US);

    static {
        TWITTER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static Date getTwitterDate(JsonObject obj, String key) {
        String str = obj.getString(key);
        return parseTwitterDate(str);
    }

    public static Date parseTwitterDate(String str) {
        try {
            Date date = TWITTER_DATE_FORMAT.parse(str);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public Date getCreatedTime() {
        return Tweet.getTwitterDate(this, "created_at");
    }

    public int getFollowersCount() {
        return getInteger("followers_count", 0);
    }

    public int getFriendsCount() {
        return getInteger("friends_count", 0);
    }

    public String getId() {
        return getString("id_str");
    }

    public String getInReplayToScreenName() {
        return getString("in_reply_to_screen_name");
    }

    public String getInReplyToStatusId() {
        return getString("in_reply_to_status_id_str");
    }

    public String getInReplyToUserId() {
        return getString("in_reply_to_user_id_str");
    }

    public String getLang() {
        return getString("lang");
    }

    public String getPlace() {
        return getString("place");
    }

    public int getRetweetCount() {
        return getInteger("retweet_count", 0);
    }

    public String getText() {
        return getString("text");
    }

    public String getTimeZone() {
        return getString("time_zone");
    }

    public TweetUser getUser() {
        return getObject("user", TweetUser.FACTORY);
    }

    public boolean isFavorited() {
        return getBoolean("favorited", false);
    }

    public boolean isFollowing() {
        return getBoolean("following", false);
    }

    public boolean isRetweeted() {
        return getBoolean("retweeted", false);
    }

    public boolean isTrancated() {
        return getBoolean("truncated", false);
    }

}
