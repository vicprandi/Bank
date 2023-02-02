package BankApplication.exception;

public class AccountDoesntExistExceptionDetails {

    public  String title;
    public  String details;

    public AccountDoesntExistExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class AccountDoesntExistExceptionDetailsBuilder {

        public String title;
        public String details;

        private AccountDoesntExistExceptionDetailsBuilder() {
        }

        public static AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder newBuilder() {
            return new AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder();
        }

        public AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AccountDoesntExistExceptionDetails build () {

            AccountDoesntExistExceptionDetails clientDoesntExistExceptionDetails = new AccountDoesntExistExceptionDetails();
            clientDoesntExistExceptionDetails.title = this.title;
            clientDoesntExistExceptionDetails.details = this.details;
            return clientDoesntExistExceptionDetails;

        }
    }
}