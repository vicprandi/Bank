package bank.customer.exceptions.details;

public class CustomerDoesntExistExceptionDetails {

    public  String title;
    public  String details;

    public CustomerDoesntExistExceptionDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public static final class ClientDoesntExistExceptionDetailsBuilder {

        public String title;
        public String details;

        private ClientDoesntExistExceptionDetailsBuilder() {
        }

        public static CustomerDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder newBuilder() {
            return new CustomerDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder();
        }

        public CustomerDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CustomerDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public CustomerDoesntExistExceptionDetails build () {

            CustomerDoesntExistExceptionDetails customerDoesntExistExceptionDetails = new CustomerDoesntExistExceptionDetails();
            customerDoesntExistExceptionDetails.title = this.title;
            customerDoesntExistExceptionDetails.details = this.details;
            return customerDoesntExistExceptionDetails;

        }
    }
}
