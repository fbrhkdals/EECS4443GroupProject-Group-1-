# Blabla Chat App(quickcomms)

Blabla is a real-time messaging app designed for chatting with friends. The app includes essential features like account creation, profile updates, friend management, and chat functionality.

## Features

### User Features:
- **Account Creation**: Users can create an account with their email.
- **Login**: Users can log in with their credentials after registration.
- **Profile Update**: Users can update their profile picture and information.
- **Auto-login**: The app stores login information for seamless auto-login.
- **Friend Management**: Add or delete friends from your friend list. The list is sorted alphabetically. Also deleting friend will also delete chat room.
- **Friend Search**: Users can search for friends by name, and mutual friends will be suggested.
- **Friend Profile**: Tap on a friend’s profile to view detailed information.
- **Chatting**: Create and manage chat rooms, and send real-time messages.

### Chat Features:
- **Unread Messages**: Unread messages are displayed at the top of the chat list.
- **Message Sorting**: Messages are displayed in chronological order, with the latest message at the bottom.
- **Unread Count**: The number of unread messages is displayed next to each chat. If there are 10 or more unread messages, it will show "10+".

## Installation Guide

### 1. Clone the GitHub Repository

To get started, clone the project repository from GitHub to your local machine. Open your terminal or command prompt and run the following command:

```bash
git clone https://github.com/fbrhkdals/EECS4443GroupProject-Group-1-.git
```
Download the project files to your local system by cloning the repository. If you don't have Git installed, you can download and install it from [here](https://git-scm.com/).

## 2. Install Android Studio

Android Studio is the official IDE for Android development. Follow these steps to install it:

1. Go to the [Android Studio Download Page](https://developer.android.com/studio) and choose the version for your operating system.
2. Download and install Android Studio.
3. After installation, open Android Studio.

## 3. Open the Project in Android Studio

Once Android Studio is installed, follow these steps to open the project:

1. Open Android Studio.
2. Click on **Open an existing project**.
3. Navigate to the folder where you cloned the repository and select the project folder.
4. **Trust the Project**: When the project is opened, Android Studio will prompt you with a "Trust Project" message. Click **Trust Project** to continue.
5. After trusting the project, you will see the **Sync Now** button in the upper right corner. Click **Sync Now** to sync the Gradle files.

## 4. Sync Gradle

Click on **Sync Now** in the top right corner to synchronize the project with the required dependencies. This will ensure all necessary libraries and configurations are downloaded.

## 5. Run the Application

Once the project is synced, you can run the app:

1. Click the green **Run** button in Android Studio.
2. Select a device (either a physical device or an Android emulator).
3. Wait for the app to build and launch.

## Project Structure

The Blabla app is organized in the following way:

- **app/src/main/java/com/blabla**: Contains the main business logic and UI components.
  - **activities**: Activities representing each screen in the app.
  - **adapters**: Adapter classes for displaying chat and friend lists.
  - **model**: Data models (e.g., User, Message, Friend).
  - **repository**: Handles data operations, including database access.
  - **dao**: Data Access Objects for Room database integration.
  - **viewmodel**: Contains the app's ViewModel classes for managing UI-related data.
  - **utils**: Utility functions (e.g., image upload, search functionality).
- **app/src/main/res**: Contains the resources for the app.
  - **layout**: XML files for defining the UI layout.
  - **values**: Strings, colors, and styles used in the app.
  - **drawable**: Image resources for the app.
- **app/build.gradle**: Contains the build configuration and dependencies.

## Technology Stack

- **Language**: Java

### Libraries:
- **Room**: Used for local database management with SQLite (using Room for database access instead of Retrofit or Firebase).
- **MVVM Pattern**: ViewModel, LiveData, and Repository for managing app data and ensuring separation of concerns.
- **Bitmap**: Used for handling image loading and manipulation (for profile pictures instead of Glide).
- **SearchView**: Allows users to search for friends.
- **RecyclerView**: For displaying chat and friend lists efficiently.

### Tools:
- **Android Studio**
- **Gradle**
