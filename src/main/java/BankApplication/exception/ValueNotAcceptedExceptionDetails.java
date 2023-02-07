package BankApplication.exception;

public class ValueNotAcceptedExceptionDetails {


    public String title;
    public String details;

    public ValueNotAcceptedExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class valueNotAcceptedExceptionDetailsBuilder {

        public String title;
        public String details;

        private valueNotAcceptedExceptionDetailsBuilder() {
        }

        public static ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder newBuilder() {
            return new ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder();
        }

        public ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ValueNotAcceptedExceptionDetails build () {

            ValueNotAcceptedExceptionDetails valueNotAcceptedExceptionDetails = new ValueNotAcceptedExceptionDetails();
            valueNotAcceptedExceptionDetails.title = this.title;
            valueNotAcceptedExceptionDetails.details = this.details;

            return valueNotAcceptedExceptionDetails;

        }



    }

