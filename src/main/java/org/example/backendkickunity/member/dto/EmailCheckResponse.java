package org.example.backendkickunity.member.dto;

public class EmailCheckResponse {

    private boolean success;
    private String message;

    public EmailCheckResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 생성자, getter, setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
