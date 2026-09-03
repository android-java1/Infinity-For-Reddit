package ml.docilealligator.infinityforreddit.recentsearchquery;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

import java.util.List;

import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;

public class RecentSearchQueryRepository {
    private final RedditDataRoomDatabase mRedditDataRoomDatabase;
    private final String mUsername;
    private final LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    RecentSearchQueryRepository(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mUsername = username;
        mAllRecentSearchQueries = redditDataRoomDatabase.recentSearchQueryDao().getAllRecentSearchQueriesLiveData(username);
    }

    LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }

    Cursor searchRecentQueries(Context context, String queryFragment) {
        return mRedditDataRoomDatabase.searchRecentSearchHistory(context, mUsername, queryFragment);
    }
}
