# 📘 Teacher ERP App

The **Teacher ERP App** is an Android-based application developed in **Java** using **Android Studio**. It serves as the teacher's interface in a dual ERP system designed to digitize and streamline school/college classroom management. This application works alongside the **Student ERP App**, enabling real-time communication and academic data exchange between teachers and students.

---

## 📌 Overview

This app allows teachers to manage student-related tasks efficiently such as admissions, attendance, class schedules, notices, doubts, and quiz creation. It is fully integrated with **Firebase** services to handle authentication, data storage, and push notifications.

---

## 🎯 Features

- 📋 **Student Admission Management**  
  Easily admit students and store their data in Firestore.

- 🧑‍🏫 **Attendance Marking**  
  Mark and upload attendance date-wise with Firestore support.

- 🗓 **Class Schedule Management**  
  Add, edit, and update class schedules by day and time.

- 🔔 **Push Notifications**  
  Notify students instantly when schedules or notices are updated (via Firebase Cloud Messaging).

- 📢 **Notice Board**  
  Post text or PDF notices, which students can view in their app.

- ❓ **Doubt Section**  
  View and reply to student queries filtered by your name.

- 👨‍🏫 **Teacher Authentication**  
  Secure login and registration using Firebase Authentication.

---

## 🔧 Tech Stack

- **Language**: Java  
- **IDE**: Android Studio  
- **Database**: Firebase Firestore  
- **Auth & Storage**: Firebase Authentication + Storage  
- **Notifications**: Firebase Cloud Messaging (FCM)  
- **UI Tools**: RecyclerView, CardView, Glide, Material Components

---

## 🧩 Firebase Modules Used

- Firestore: For all app data (students, attendance, schedules, etc.)
- Firebase Auth: For teacher login and registration
- Firebase Storage: For storing uploaded PDFs (quizzes, notices)
- Firebase Cloud Messaging (FCM): For push notifications to Student ERP

---

## 🔗 Related Project

This app works in sync with the **Student ERP App**, which handles the student-side interface of the system.

👉 **Student ERP GitHub Repository**: _[Add Link Here]_  

---

## 📁 Project Structure

    TeacherERP/
    ├── java/com/yourpackage/teachererp/
    │ ├── activities/ # UI screens like Dashboard, Login, etc.
    │ ├── adapters/ # RecyclerView adapters
    │ ├── models/ # Model classes (e.g., Student, Schedule)
    │ └── utils/ # Helper functions
    ├── res/layout/ # XML layouts
    ├── res/drawable/ # Images, icons
    └── AndroidManifest.xml

---

## 🖼 Screenshots

<img width="2245" height="914" alt="teacher" src="https://github.com/user-attachments/assets/abdbd84c-4bf2-4379-9b9e-8aca5628cb94" />


---

## 🧪 How to Run

1. Clone the repo
2. Open with Android Studio
3. Connect your Firebase project (via `google-services.json`)
4. Build and Run on Emulator or Physical Device

---

## 📬 Feedback & Contributions

Feel free to raise issues or submit pull requests for suggestions, bug fixes, or new feature ideas. All contributions are welcome!
