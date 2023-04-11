package bank.customer.exceptions.details;

public class CpfAlreadyExistsExceptionDetails {


    public String title;
    public String details;

    public CpfAlreadyExistsExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class cpfAlreadyExistDetailsBuilder {

        public String title;
        public String details;

        private cpfAlreadyExistDetailsBuilder() {
        }

        public static CpfAlreadyExistsExceptionDetails.cpfAlreadyExistDetailsBuilder newBuilder() {
            return new CpfAlreadyExistsExceptionDetails.cpfAlreadyExistDetailsBuilder();
        }

        public CpfAlreadyExistsExceptionDetails.cpfAlreadyExistDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CpfAlreadyExistsExceptionDetails.cpfAlreadyExistDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public CpfAlreadyExistsExceptionDetails build () {

            CpfAlreadyExistsExceptionDetails cpfAlreadyExistsExceptionDetails = new CpfAlreadyExistsExceptionDetails();
            cpfAlreadyExistsExceptionDetails.title = this.title;
            cpfAlreadyExistsExceptionDetails.details = this.details;
            return cpfAlreadyExistsExceptionDetails;

        }
    }
}
