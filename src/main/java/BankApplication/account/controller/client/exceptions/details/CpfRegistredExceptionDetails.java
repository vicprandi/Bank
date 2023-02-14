package BankApplication.account.controller.client.exceptions.details;

public class CpfRegistredExceptionDetails {

    public String title;
    public String details;

    public CpfRegistredExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class cpfRegistredExceptionDetailsBuilder {

        public String title;
        public String details;

        private cpfRegistredExceptionDetailsBuilder() {
        }

        public static cpfRegistredExceptionDetailsBuilder newBuilder() {
            return new cpfRegistredExceptionDetailsBuilder();
        }

        public cpfRegistredExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public cpfRegistredExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public CpfRegistredExceptionDetails build () {

            CpfRegistredExceptionDetails cpfRegistredExceptionDetails = new CpfRegistredExceptionDetails();
            cpfRegistredExceptionDetails.title = this.title;
            cpfRegistredExceptionDetails.details = this.details;
            return cpfRegistredExceptionDetails;

        }

    }
}
