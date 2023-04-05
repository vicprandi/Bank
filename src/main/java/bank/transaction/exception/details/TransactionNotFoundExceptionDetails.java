package bank.transaction.exception.details;

public class TransactionNotFoundExceptionDetails {
    public String title;
    public String details;

    public TransactionNotFoundExceptionDetails() {}

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class transactionNotFoundExceptionDetailsBuilder {
        public String title;
        public String details;

        private transactionNotFoundExceptionDetailsBuilder() {
        }

        public static TransactionNotFoundExceptionDetails.transactionNotFoundExceptionDetailsBuilder newBuilder() {
            return new TransactionNotFoundExceptionDetails.transactionNotFoundExceptionDetailsBuilder();
        }

        public TransactionNotFoundExceptionDetails.transactionNotFoundExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TransactionNotFoundExceptionDetails.transactionNotFoundExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public TransactionNotFoundExceptionDetails build() {

            TransactionNotFoundExceptionDetails transactionNotFoundExceptionDetails = new TransactionNotFoundExceptionDetails();
            transactionNotFoundExceptionDetails.title = this.title;
            transactionNotFoundExceptionDetails.details = this.details;

            return transactionNotFoundExceptionDetails;

        }
    }
}
