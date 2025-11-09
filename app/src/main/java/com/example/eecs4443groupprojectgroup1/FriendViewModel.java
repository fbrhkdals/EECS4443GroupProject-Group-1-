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

    // Get FriendRequest
    public Friend getFriendRequest(int userId, int friendId) {
        return friendRepository.getFriendRequest(userId, friendId);
    }

    // Method to send a friend request (status = "pending")
    public void sendFriendRequest(int userId, int friendId) {
        friendRepository.sendFriendRequest(userId, friendId);
    }

    // Method to update the status of the friend request (accepted or rejected)
    public void updateFriendRequestStatus(int userId, int friendId, String status) {
        friendRepository.updateFriendRequestStatus(userId, friendId, status);
    }

    // ViewModelFactory to instantiate the ViewModel with FriendRepository
    public static class Factory implements ViewModelProvider.Factory {
        private final AppDatabase appDatabase;

        public Factory(AppDatabase appDatabase) {
            this.appDatabase = appDatabase;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            // Create a new instance of FriendRepository using the appDatabase
            FriendRepository repository = new FriendRepository(appDatabase);
            // Return a new instance of FriendViewModel
            return (T) new FriendViewModel(repository);
        }
    }

    // Get the list of received friend requests by status (e.g., "pending")
    public LiveData<List<Friend>> getReceivedFriendRequestsByStatus(int userId, String status) {
        return friendRepository.getReceivedFriendRequestsByStatus(userId, status);
    }

    // Sorted version
    public LiveData<List<Friend>> getReceivedFriendRequestsByStatusSorted(int userId, String status) {
        return friendRepository.getReceivedFriendRequestsByStatusSorted(userId, status);
    }
}