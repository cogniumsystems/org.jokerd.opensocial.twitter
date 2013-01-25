package org.jokerd.opensocial.tweeter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jokerd.opensocial.api.model.ActivityEntry;
import org.jokerd.opensocial.api.model.ActivityObject;
import org.jokerd.opensocial.api.model.DomainName;
import org.jokerd.opensocial.api.model.MediaLink;
import org.jokerd.opensocial.api.model.ObjectId;
import org.jokerd.opensocial.api.model.Person;
import org.jokerd.opensocial.cursors.AbstractActivityBuilder;
import org.jokerd.opensocial.cursors.ActivityEntryUtil;
import org.jokerd.opensocial.tweeter.model.Tweet;
import org.jokerd.opensocial.tweeter.model.Tweet.TweetUser;
import org.jokerd.opensocial.tweeter.model.TweetParser;
import org.jokerd.opensocial.tweeter.model.TweetParser.FormatListener;
import org.ubimix.commons.json.JsonArray;
import org.ubimix.commons.json.JsonObject;
import org.ubimix.commons.json.ext.DateFormatter;
import org.ubimix.commons.json.ext.FormattedDate;

/**
 * @author kotelnikov
 */
public class TwitterActivityBuilder extends AbstractActivityBuilder {

    private final DomainName DOMAIN_NAME = new DomainName("twitter.com");

    private final JsonArray fFeed;

    public TwitterActivityBuilder(JsonArray feed) {
        fFeed = feed;
    }

    public ActivityEntry buildActivityEntry(Tweet tweet) {

        ActivityEntry result = new ActivityEntry();
        ObjectId entryId = getId("activity", tweet.getId());
        result.setId(entryId.toString());

        FormattedDate updateTime = formatDate(tweet.getCreatedTime());
        result.setPublished(updateTime);

        String title = tweet.getText();
        result.setTitle(title);

        ActivityObject author = getAuthorObject(tweet);
        result.setActor(author);

        result.setVerb("post");

        ActivityObject activityObject = buildActivityObject(tweet);
        result.setObject(activityObject);

        ActivityObject target = buildTargetObject(tweet);
        result.setTarget(target);

        JsonObject twitterObject = new JsonObject();
        result.setValue("tweet-data", twitterObject);
        copyFields(
            tweet,
            twitterObject,
            "retweeted",
            "possibly_sensitive",
            "contributors",
            "in_reply_to_screen_name",
            "place",
            "retweet_count",
            "entities",
            "media",
            "urls",
            "user_mentions",
            "hashtags https",
            "in_reply_to_user_id",
            "geo",
            "favorited",
            "in_reply_to_status_id_str",
            "in_reply_to_status_id",
            "source",
            "in_reply_to_user_id_str",
            "coordinates",
            "truncated");

        return result;
    }

    private ActivityObject buildActivityObject(Tweet tweet) {
        ActivityObject obj = new ActivityObject();

        ObjectId id = getId("tweet", tweet.getId());
        obj.setId(id);
        // FIXME
        obj.setObjectType("feed-entry");

        ActivityObject author = getAuthorObject(tweet);
        obj.setAuthor(author);

        Date date = tweet.getCreatedTime();
        FormattedDate publishTime = DateFormatter.formatDate(date);
        obj.setPublished(publishTime);
        obj.setUpdated(publishTime);

        String title = tweet.getText();
        final List<String> urlList = new ArrayList<String>();
        TweetParser tweetParser = new TweetParser();
        FormatListener listener = new TweetParser.FormatListener() {
            @Override
            public void onUrl(String value) {
                urlList.add(value);
                super.onUrl(value);
            }
        };
        tweetParser.parse(title, listener);
        String content = listener.toString();
        String url = !urlList.isEmpty() ? urlList.get(0) : null;
        obj.setUrl(url);
        obj.setDisplayName(title);
        obj.setContent(content);

        return obj;
    }

    private ActivityObject buildTargetObject(Tweet tweet) {
        ActivityObject obj = new ActivityObject();
        TweetUser user = tweet.getUser();
        // FIXME: create a unique identifier for the tweet flow
        // where this tweet is published
        ObjectId id = getId("twittimeline", user.getId());
        obj.setId(id);
        obj.setObjectType("twitter");
        // FIXME: change the display name
        // It should reflect the name of the *stream* where this tweet is
        // published.
        obj.setDisplayName("Twitter: " + user.getDisplayName() + "");
        return obj;
    }

    private void copyFields(JsonObject from, JsonObject to, String... fields) {
        for (String field : fields) {
            Object obj = from.getValue(field);
            to.setValue(field, obj);
        }
    }

    private FormattedDate formatDate(Date date) {
        return DateFormatter.formatDate(date);
    }

    public List<ActivityEntry> getActivities() {
        List<ActivityEntry> result = new ArrayList<ActivityEntry>();
        for (Tweet tweet : fFeed.getList(Tweet.FACTORY)) {
            ActivityEntry activityEntry = buildActivityEntry(tweet);
            if (activityEntry != null) {
                result.add(activityEntry);
            }
        }
        Collections.sort(result, ActivityEntryUtil.ENTRY_COMPARATOR);
        return result;
    }

    private Person getAuthorObject(Tweet tweet) {
        Person obj = new Person();
        TweetUser user = tweet.getUser();
        ObjectId id = getId("author", user.getId());
        obj.setId(id);
        obj.setObjectType("person");
        obj.setDisplayName(user.getDisplayName());
        obj.setContent(user.getDescription());
        obj.setUrl(user.getUrl());
        String screenName = user.getScreenName();
        String profileUrl = "https://twitter.com/#!/" + screenName;
        obj.setProfileUrl(profileUrl);
        FormattedDate time = formatDate(user.getProfileCreationTime());
        obj.setPublished(time);
        String imageUrl = user.getImage();
        if (imageUrl != null) {
            MediaLink link = new MediaLink();
            link.setUrl(imageUrl);
            obj.setMediaLink(link);
        }
        return obj;
    }

    @Override
    protected DomainName getDomainName() {
        return DOMAIN_NAME;
    }

}