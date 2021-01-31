package com.github.foxycom.clone_detector;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class Notifier {
    public static void notify(String title, String content) {
        notify(title, content, NotificationType.INFORMATION);
    }

    public static void notify(String title, String content, NotificationType type) {
        Notifications.Bus.notify(
              new Notification("CloneNotification", title, content, type));
    }
}
