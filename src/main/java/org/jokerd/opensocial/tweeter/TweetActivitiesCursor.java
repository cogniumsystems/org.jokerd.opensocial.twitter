package org.jokerd.opensocial.tweeter;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jokerd.opensocial.api.model.ActivityEntry;
import org.jokerd.opensocial.api.model.ObjectId;
import org.jokerd.opensocial.cursors.ActivityListCursor;
import org.jokerd.opensocial.cursors.IActivityCursor;
import org.jokerd.opensocial.cursors.StreamException;
import org.jokerd.opensocial.oauth.OAuthHelper;

import org.ubimix.commons.cursor.ICursor;
import org.ubimix.commons.cursor.SequentialCursor;
import org.ubimix.commons.json.JsonArray;

/**
 * @author kotelnikov
 */
public class TweetActivitiesCursor
    extends
    SequentialCursor<ActivityEntry, StreamException> implements IActivityCursor {

    private static Logger log = Logger.getLogger(TweetActivitiesCursor.class
        .getName());

    // https://dev.twitter.com/docs/api/1/get/statuses/user_timeline

    // https://api.twitter.com/1.1/friends/ids.json?cursor=-1&screen_name=USER_NAME

    // https://api.twitter.com/1/statuses/user_timeline.json?include_entities=true&exclude_replies=false&include_rts=true&screen_name=USER_NAME&count=200

    private static String TWITTER_FRIENDS =
    "http://api.twitter.com/1/statuses/home_timeline.json?include_entities=true&count=200";

    private final OAuthHelper fOAuthHelper;

    // private long fMaxId = -1;
    private int fPage;

    private final String fUrl;

    public TweetActivitiesCursor(OAuthHelper oauthHelper) {
        fOAuthHelper = oauthHelper;
        fUrl = TWITTER_FRIENDS;
    }

    
    public TweetActivitiesCursor(OAuthHelper oauthHelper, String url) {
        fOAuthHelper = oauthHelper;
        fUrl = url;
    }

    private StreamException handleError(String msg, Throwable t) {
        if (t instanceof StreamException) {
            return (StreamException) t;
        }
        log.log(Level.FINE, msg, t);
        return new StreamException(t);
    }

    @Override
    protected ICursor<ActivityEntry, StreamException> loadNextCursor(
        ICursor<ActivityEntry, StreamException> cursor) throws StreamException {
        try {
            String url = fUrl;
            if (fPage > 0) {
                url += "&page=" + fPage;
            }
            fPage++;
            HashMap<String, String> map = new HashMap<String, String>();
            System.out.println(url);
            String json = fOAuthHelper.call(url, map.entrySet());
            JsonArray tweets = JsonArray.FACTORY.newValue(json);

            TwitterActivityBuilder builder = new TwitterActivityBuilder(tweets);
            List<ActivityEntry> activities = builder.getActivities();
            url = null;
            if (activities.isEmpty()) {
                return null;
            }
            long nextId = -1;
            for (ActivityEntry entry : activities) {
                ObjectId id = entry.getId();
                String localId = id.getLocalIdDecoded();
                int idx = localId.indexOf("-");
                if (idx > 0) {
                    localId = localId.substring(idx + 1);
                }
                long idNum = Long.parseLong(localId);
                if (nextId < 0) {
                    nextId = idNum;
                } else if (nextId > idNum) {
                    nextId = idNum;
                }
            }
            ActivityListCursor result = new ActivityListCursor(activities);
            return result;
        } catch (Throwable t) {
            throw handleError("Can not load a new activity cursor.", t);
        }
    }

}