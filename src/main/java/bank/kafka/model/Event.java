package bank.kafka.model;

public enum Event {
    SAVE_TRANSFER("SAVE_TRANSFER"),

    FAILED("FAILED");

    private final String event;

    private Event(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
