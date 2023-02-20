package bank.account.exceptions.details;

public class AccountAlreadyExistsExceptionDetails {

    public String title;
    public String details;

    public AccountAlreadyExistsExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class accountAlreadyExistsExceptionDetailsBuilder {

        public String title;
        public String details;

        private accountAlreadyExistsExceptionDetailsBuilder() {
        }

        public static accountAlreadyExistsExceptionDetailsBuilder newBuilder() {
            return new accountAlreadyExistsExceptionDetailsBuilder();
        }

        public accountAlreadyExistsExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public accountAlreadyExistsExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AccountAlreadyExistsExceptionDetails build () {

            AccountAlreadyExistsExceptionDetails accountAlreadyExistsExceptionDetails = new AccountAlreadyExistsExceptionDetails();
            accountAlreadyExistsExceptionDetails.title = this.title;
            accountAlreadyExistsExceptionDetails.details = this.details;

            return accountAlreadyExistsExceptionDetails;

        }


    }
}