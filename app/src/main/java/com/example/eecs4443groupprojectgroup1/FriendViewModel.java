package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.List;

// ViewModel for managing friends data
public class FriendViewModel extends ViewModel {
    private final FriendRepository friendRepository;

    // Constructor to initialize the repository
    public FriendViewModel(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    // Method to get the list of friends of user1, sorted by common friends with user2
    // This method now takes both user IDs and returns LiveData
    public LiveData<List<FriendRepository.UserWithCommonFriends>> getSortedFriendsByCommonFriends(int userId1) {
        return friendRepository.getSortedFriendsByCommonFriends(userId1);
    }

    // ViewModelFactory to instantiate the ViewModel with FriendRepository
    public static class Factory implements ViewModelProvider.Factory {
        private final AppDatabase appDatabase;

        public Factory(AppDatabase appDatabase) {
            this.appDatabase = appDatabase;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            FriendRepository repository = new FriendRepository(appDatabase);
            return (T) new FriendViewModel(repository);
        }
    }
}