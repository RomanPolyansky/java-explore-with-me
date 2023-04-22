package ru.practicum.exception;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorMessage {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ErrorMessage(String status, String reason, String message, Date timestamp) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.timestamp = dateFormat.format(timestamp);
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}