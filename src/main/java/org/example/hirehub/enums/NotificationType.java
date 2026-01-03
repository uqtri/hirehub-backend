package org.example.hirehub.enums;

public enum NotificationType {
    REQUEST,
    MESSAGE,
    INTERVIEW,
    SYSTEM,
    JOB_UPDATE,
    APPLICATION_STATUS,
    FRIEND_REQUEST,
    FRIEND_ACCEPT,
    INTERVIEW_INVITATION,      // New: When invited to interview
    INTERVIEW_COMPLETED,       // New: When applicant completes async interview
    INTERVIEW_RESULT,          // New: When interview result is ready
    INTERVIEW_REMINDER,        // New: Reminder before interview starts
    INTERVIEW_CANCELLED        // New: When interview is cancelled
}
