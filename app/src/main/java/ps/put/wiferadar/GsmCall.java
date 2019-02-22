package ps.put.wiferadar;

public class GsmCall {
    private String displayName;
    private Status status;


    public GsmCall(String displayName, Status status) {
        this.displayName = displayName;
        this.status = status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        CONNECTING,
        DIALING,
        RINGING,
        ACTIVE,
        DISCONNECTED,
        UNKNOWN
    }
}
