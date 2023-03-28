package bank.transaction.exception.details;

public class TransferValidationExceptionDetails {
    public String title;
    public String details;

    public TransferValidationExceptionDetails(){
    }
    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }


    public static final class transferValidationExceptionDetailsBuilder {
        public String title;
        public String details;

        private transferValidationExceptionDetailsBuilder() {
        }

        public static TransferValidationExceptionDetails.transferValidationExceptionDetailsBuilder newBuilder() {
            return new TransferValidationExceptionDetails.transferValidationExceptionDetailsBuilder();
        }

        public TransferValidationExceptionDetails.transferValidationExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TransferValidationExceptionDetails.transferValidationExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public TransferValidationExceptionDetails build() {

            TransferValidationExceptionDetails transferValidationExceptionDetails = new TransferValidationExceptionDetails();
            transferValidationExceptionDetails.title = this.title;
            transferValidationExceptionDetails.details = this.details;

            return transferValidationExceptionDetails;

        }
    }


}
