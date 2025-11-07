package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserDao;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendRepository {

    private FriendsDao friendsDao;
    private UserDao userDao;

    // Constructor to initialize DAOs for accessing data in the database
    public FriendRepository(AppDatabase db) {
        this.friendsDao = db.friendDao();  // DAO for managing friendships
        this.userDao = db.userDao();  // DAO for managing user data
    }

    // Fetch the list of friends for a specific user by their userId
    public List<User> getFriends(int userId) {
        List<Integer> friendIds = friendsDao.getFriends(userId);  // Get the list of friend IDs for the given user
        List<User> friends = new ArrayList<>();

        // Retrieve the User data for each friendId from the UserDao
        for (int friendId : friendIds) {
            User friend = userDao.getUserByIdSync(friendId);  // Synchronize call to fetch the user object
            if (friend != null) {
                friends.add(friend);  // Add the friend to the list
            }
        }
        return friends;  // Return the list of friends
    }

    // Calculate the number of common friends between two users
    public int getCommonFriendsCount(int userId1, int userId2) {
        return friendsDao.getCommonFriendsCount(userId1, userId2);  // Return the count of common friends between the two users
    }

    // Fetch the sorted list of friends of user1, based on the common friends count with user2
    public LiveData<List<UserWithCommonFriends>> getSortedFriendsByCommonFriends(int userId1) {
        MutableLiveData<List<UserWithCommonFriends>> liveData = new MutableLiveData<>();

        new Thread(() -> {
            // Get user1's friend list
            List<User> friends = getFriends(userId1);
            List<UserWithCommonFriends> friendsWithCommonCount = new ArrayList<>();

            // For each friend, calculate the common friends count with other friends
            for (User friend : friends) {
                // Skip adding the user itself to the list
                if (friend.id == userId1) continue;  // If friend's ID is the same as userId1, skip

                // Get the common friends count with all of userId1's friends
                int commonFriendsCount = 0;
                for (User otherFriend : friends) {
                    if (otherFriend.id != friend.id) {
                        // Count the common friends between 'friend' and 'otherFriend'
                        commonFriendsCount += getCommonFriendsCount(friend.id, otherFriend.id);
                    }
                }

                // Store the result
                friendsWithCommonCount.add(new UserWithCommonFriends(friend, commonFriendsCount));
            }

            // Sort the list of friends by common friends count in descending order
            friendsWithCommonCount.sort((o1, o2) -> Integer.compare(o2.commonFriendsCount, o1.commonFriendsCount));

            // Limit the result to 50 friends
            List<UserWithCommonFriends> limitedFriends = new ArrayList<>(friendsWithCommonCount.subList(0, Math.min(50, friendsWithCommonCount.size())));

            // Get all users from the database
            List<User> allUsers = userDao.getAllUsers();
            int remainingSlots = 50 - limitedFriends.size();
            int addedNonFriends = 0;

            for (User user : allUsers) {
                // Skip users that are already friends or the user itself
                if (user.id == userId1 || isFriend(user, friends)) {
                    continue;  // Skip if it's the user themselves or a friend
                }

                if (addedNonFriends < remainingSlots) {
                    limitedFriends.add(new UserWithCommonFriends(user, 0));  // Add non-friend user with 0 common friends
                    addedNonFriends++;
                } else {
                    break;  // Stop if we've added 50 users
                }
            }

            liveData.postValue(limitedFriends);  // Post the final list of users to LiveData
        }).start();

        return liveData;  // Return the LiveData to the UI
    }

    // Helper method to check if the user is in the friends list
    private boolean isFriend(User user, List<User> friends) {
        for (User friend : friends) {
            if (friend.id == user.id) {
                return true;  // User is a friend
            }
        }
        return false;  // User is not a friend
    }

    // Helper class to pair a user with their common friends count
    public static class UserWithCommonFriends {
        User user;  // The User object representing the friend
        int commonFriendsCount;  // The count of common friends with another user

        // Constructor to initialize the User and its common friends count
        public UserWithCommonFriends(User user, int commonFriendsCount) {
            this.user = user;
            this.commonFriendsCount = commonFriendsCount;
        }
    }
}