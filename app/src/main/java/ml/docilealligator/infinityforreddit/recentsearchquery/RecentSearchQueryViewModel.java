package ml.docilealligator.infinityforreddit.recentsearchquery;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;

public class RecentSearchQueryViewModel extends ViewModel {
    private final RecentSearchQueryRepository mRepository;
    private final LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    public RecentSearchQueryViewModel(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mRepository = new RecentSearchQueryRepository(redditDataRoomDatabase, username);
        mAllRecentSearchQueries = mRepository.getAllRecentSearchQueries();
    }

    public LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }

    public void findMatchingRecentSearches(Context context, String queryFragment) {
        Cursor matches = mRepository.searchRecentQueries(context, queryFragment);
        if (matches != null) {
            matches.close();
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final RedditDataRoomDatabase mRedditDataRoomDatabase;
        private final String mUsername;

        public Factory(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
            mRedditDataRoomDatabase = redditDataRoomDatabase;
            mUsername = username;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RecentSearchQueryViewModel(mRedditDataRoomDatabase, mUsername);
        }
    }
}
