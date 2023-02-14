package BankApplication.account.controller.client.exceptions.details;

public class ClientDoesntExistExceptionDetails {

    public  String title;
    public  String details;

    public ClientDoesntExistExceptionDetails() {
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

        public static ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder newBuilder() {
            return new ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder();
        }

        public ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ClientDoesntExistExceptionDetails build () {

            ClientDoesntExistExceptionDetails clientDoesntExistExceptionDetails = new ClientDoesntExistExceptionDetails();
            clientDoesntExistExceptionDetails.title = this.title;
            clientDoesntExistExceptionDetails.details = this.details;
            return clientDoesntExistExceptionDetails;

        }
    }
}
