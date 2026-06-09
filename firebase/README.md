# Firebase setup

1. Register `com.example.lostandfound` in Firebase Console.
2. Put `google-services.json` into `app/`.
3. Enable Anonymous authentication and create Firestore.
4. Publish `firestore.rules` from the project root.
5. Add Remote Config parameters:
   - `welcome_message` - string.
   - `experimental_feature_enabled` - boolean.
6. Copy the FCM token from Logcat using the `PushMessagingService` tag.
7. Send a data message with `send-test-push.ps1`.

For FCM HTTP v1, create a service-account key in Firebase Console:
Project settings -> Service accounts -> Generate new private key.
Keep the downloaded JSON outside Git.
