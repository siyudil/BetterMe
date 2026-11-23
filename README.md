# BetterMe: Personal Wellness Management App

BetterMe is an Android application that promotes personal wellness and helps users manage their daily health activities. It is built with **Android Studio** and **Kotlin**, and encourages users to build healthy habits, track their mood and stay hydrated with smart reminders, all in a clean and intuitive interface.

## Features

### Daily Habit Tracker
- Add, edit and delete health habits such as hydrating, exercising or meditating.
- Progress bars show daily progress so users stay motivated and engaged.

### Mood Journal
- Record your mood using emojis, saved with a timestamp.
- View past entries as a list or in a calendar view to spot mood patterns over time.

### Hydration Reminders
- Smart reminders built with Android's **WorkManager / AlarmManager** notify the user to drink water at intervals they choose throughout the day.

### Mood Trend Chart
- A simple weekly mood chart built with **MPAndroidChart** shows how mood changes during the week.

### Local Data Storage
- Preferences, habits and mood logs are stored on the device using **SharedPreferences**, so data is kept when the user returns to the app. No database is required.

### Responsive Design
- The layout adapts to different screen sizes and orientations, for both smartphones and tablets.

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| IDE | Android Studio |
| Reminders | WorkManager / AlarmManager |
| Charts | MPAndroidChart |
| Local storage | SharedPreferences |
| Platform | Android (phones and tablets) |

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/siyudil/BetterMe.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync finish.
4. Run the app on an emulator or a physical Android device.

## Screenshots

_Add screenshots of the Habit Tracker, Mood Journal and Mood Chart screens here._

<!-- Example:
![Habit Tracker](screenshots/habits.png)
-->

## Author

**Siyumi Fonseka**
Data Science undergraduate, Sri Lanka Institute of Information Technology (SLIIT)
[LinkedIn](https://linkedin.com/in/siyumi-fonseka-61b233385) · [GitHub](https://github.com/siyudil) · [Portfolio](https://siyumi-fonseka.vercel.app)
