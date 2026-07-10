package com.lecturboxd.dto.response;

/**
 * EN: Response summarizing a development-only user deletion (user and OTP cleanup).
 * KA: პასუხი, რომელიც აჯამებს მხოლოდ დეველოპმენტის მომხმარებლის წაშლას (მომხმარებელი და OTP გასუფთავება).
 */
public class DevDeleteResponse {

    private String message;
    private String email;
    private boolean userDeleted;
    private int verificationCodesDeleted;

    public DevDeleteResponse() {
    }

    public DevDeleteResponse(String message, String email, boolean userDeleted, int verificationCodesDeleted) {
        this.message = message;
        this.email = email;
        this.userDeleted = userDeleted;
        this.verificationCodesDeleted = verificationCodesDeleted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUserDeleted() {
        return userDeleted;
    }

    public void setUserDeleted(boolean userDeleted) {
        this.userDeleted = userDeleted;
    }

    public int getVerificationCodesDeleted() {
        return verificationCodesDeleted;
    }

    public void setVerificationCodesDeleted(int verificationCodesDeleted) {
        this.verificationCodesDeleted = verificationCodesDeleted;
    }
}
