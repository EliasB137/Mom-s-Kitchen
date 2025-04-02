package il.cshaifasweng.OCSFMediatorExample.client.events;

public class LoginResultEvent {
    private String message;
    private String role;
    private int id;

    public LoginResultEvent(String message,String role,int id) {
        this.message = message;
        this.role = role;
        this.id = id;
    }
    public LoginResultEvent() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

}
