package reader.softech.com.zcommerce.Model;

public class ContactUsInfo {

    private String userEmail,userMessage;


    public ContactUsInfo(){

    }

    public ContactUsInfo(String userEmail, String userMessage) {
        this.userEmail = userEmail;
        this.userMessage = userMessage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}
