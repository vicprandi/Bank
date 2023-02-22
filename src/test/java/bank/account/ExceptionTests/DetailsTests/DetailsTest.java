package bank.account.ExceptionTests.DetailsTests;

import bank.account.exceptions.details.AccountAlreadyExistsExceptionDetails;
import bank.account.exceptions.details.AccountDoesntExistExceptionDetails;
import bank.account.exceptions.details.CpfDoesntExistExceptionDetails;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class DetailsTest {

    @Test
    public void testAccountAlreadyExistsExceptionDetails(){
        String title = "Account already exists";
        String details = "The account with ID 123456789 already exists.";
        AccountAlreadyExistsExceptionDetails detailsObj = new AccountAlreadyExistsExceptionDetails();
        detailsObj.title = title;
        detailsObj.details = details;

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }
    @Test
    public void testAccountAlreadyExistsExceptionDetailsBuilder() {
        String title = "Account already exists";
        String details = "The account with ID 123456789 already exists.";

        AccountAlreadyExistsExceptionDetails detailsObj = AccountAlreadyExistsExceptionDetails.accountAlreadyExistsExceptionDetailsBuilder
                .newBuilder()
                .title(title)
                .details(details)
                .build();

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }

    @Test
    public void testAccounDoesntExistsExceptionDetailsBuilder() {
        String title = "Account not found";
        String details = "The account with ID 1234 was not found";
        AccountDoesntExistExceptionDetails detailsObj = AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .title(title)
                .details(details)
                .build();

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }

    @Test
    public void testAccounDoesntExistsExceptionDetails() {
        String title = "Account not found";
        String details = "The account with ID 1234 was not found";
        AccountDoesntExistExceptionDetails detailsObj = new AccountDoesntExistExceptionDetails();
        detailsObj.title = title;
        detailsObj.details = details;

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }

    @Test
    public void testCpfDoesntExistExceptionDetails() {
        String title = "Cpf doesnt exists";
        String details = "The cpf 12345678900 doesnt exist";
        CpfDoesntExistExceptionDetails detailsObj = new CpfDoesntExistExceptionDetails();
        detailsObj.title = title;
        detailsObj.details = details;

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }

    @Test
    public void testCpfDoesntExistExceptionDetailsBuilder() {
        String title = "Cpf doesnt exists";
        String details = "The cpf 12345678900 doesnt exist";
        CpfDoesntExistExceptionDetails detailsObj = CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder
                .newBuilder()
                .title(title)
                .details(details)
                .build();

        assertEquals(title, detailsObj.getTitle());
        assertEquals(details, detailsObj.getDetails());
    }
}
