package bank.account.exceptions.details;

public class CpfDoesntExistExceptionDetails {


    public String title;
    public String details;

    public CpfDoesntExistExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class cpfDoesntExistDetailsBuilder {

        public String title;
        public String details;

        private cpfDoesntExistDetailsBuilder() {
        }

        public static CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder newBuilder() {
            return new CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder();
        }

        public CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public CpfDoesntExistExceptionDetails build () {

            CpfDoesntExistExceptionDetails cpfDoesntExistExceptionDetails = new CpfDoesntExistExceptionDetails();
            cpfDoesntExistExceptionDetails.title = this.title;
            cpfDoesntExistExceptionDetails.details = this.details;
            return cpfDoesntExistExceptionDetails;

        }
    }

}
