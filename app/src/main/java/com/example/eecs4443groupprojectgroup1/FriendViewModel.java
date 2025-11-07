package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.List;

public class FriendViewModel extends ViewModel {
    private final FriendRepository friendRepository;

    // Constructor to initialize the repository
    public FriendViewModel(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    // Method to get the list of friends of user1, sorted by common friends with user2
    public LiveData<List<FriendRepository.UserWithCommonFriends>> getSortedFriendsByCommonFriends(int userId1) {
        return friendRepository.getSortedFriendsByCommonFriends(userId1);
    }

    // Method to send a friend request (status = "pending")
    public void sendFriendRequest(int userId, int friendId) {
        friendRepository.sendFriendRequest(userId, friendId);
    }

    // Method to update the status of the friend request (accepted or rejected)
    public void updateFriendRequestStatus(int userId, int friendId, String status) {
        friendRepository.updateFriendRequestStatus(userId, friendId, status);
    }

    // Method to get all received friend requests (status = "pending")
    public LiveData<List<Friend>> getReceivedRequests(int userId) {
        return friendRepository.getReceivedRequests(userId);
    }

    // Method to get all sent friend requests (status = "pending")
    public LiveData<List<Friend>> getSentRequests(int userId) {
        return friendRepository.getSentRequests(userId);
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