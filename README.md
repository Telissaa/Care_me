# 📱 Activity & Routine Manager Care me

A modern Android application designed to help users manage tasks, routines, and action plans. It allows users to create detailed, rich-text activity steps, schedule context-specific notifications, and fetch real-time data like the UV Index for specific tasks.

## ✨ Features

*   **Home Dashboard**: A clean starting screen featuring a grid/list of icons. Each icon serves as a shortcut to a specific activity or task view.
*   **Rich-Text Activity Views**: Inside each activity, users can write down a detailed list of steps and descriptions. The built-in editor supports rich-text formatting, including:
    *   **Bold text**
    *   Bullet points and numbered lists
    *   Custom text colors
*   **Real-Time UV Index Tracker**: A dedicated view integrates with an external API to fetch and display the current UV Index, adding dynamic, real-world context to specific outdoor routines.
*   **Contextual Notification Drawer**: A convenient right-side sliding panel (End Drawer) available on each activity screen, displaying reminders specifically tied to that current activity.
*   **Advanced Custom Reminders**: Users have full control over notifications:
    *   Set the exact trigger time.
    *   Configure frequency and recurrence rules.
    *   Define a custom notification title.
    *   **Expandable Details**: Expanding the notification reveals a long, rich-text description (following the same formatting rules as the activity view).

## 🛠 Tech Stack

The project is built entirely in **Kotlin** and follows modern Android development standards.

*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 guidelines.
*   **Architecture**: **MVVM** (Model-View-ViewModel) integrated with **Clean Architecture** principles.
*   **Dependency Injection**: [Koin](https://insert-koin.io/) (`koin-androidx-compose`) for lightweight and purely Kotlin-based DI.
*   **Local Database**: [Room](https://developer.android.com/training/data-storage/room) handling local SQLite storage, fully observing data changes via asynchronous `Flow` streams.
*   **Networking**: **Retrofit2** & **OkHttp** for REST API calls, with **Kotlinx Serialization** for parsing JSON responses (used for fetching UV Index data).
*   **Navigation**: **Jetpack Navigation Compose** utilizing `NavHost` and type-safe routing powered by Kotlin `sealed class` / `sealed interface`.
*   **Asynchrony**: **Kotlin Coroutines** and **Flow** for smooth, non-blocking background operations and reactive UI updates.

## 🏗 Architecture Overview

The application follows the **Clean Architecture** pattern to ensure separation of concerns, testability, and scalability:

1.  **UI Layer (Presentation)**: Contains Jetpack Compose screens, layouts, and components. It observes state from the ViewModels.
2.  **ViewModel Layer**: Manages UI state and events. Interacts with the Repository layer via Coroutines to fetch data.
3.  **Repository Layer**: Acts as the single source of truth. It mediates between the local database and remote APIs, providing clean data streams (`Flow`) to the ViewModels.
4.  **Data Layer**: 
    *   **Local (Room & DAOs)**: Handles local SQLite database operations (user activities, rich-text content, notification configurations).
    *   **Remote (Retrofit)**: Handles network requests to external APIs (e.g., fetching the UV Index).

## 🚀 Getting Started

### Prerequisites
*   Android Studio (Latest stable version recommended)
*   Kotlin 1.9+
*   Minimum SDK: 24 (Adjust based on your project settings)