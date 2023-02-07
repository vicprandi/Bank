package BankApplication.handler;


import BankApplication.exception.ValueNotAcceptedException;
import BankApplication.exception.ValueNotAcceptedExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValueNotAcceptedExceptionHandler {
        @ExceptionHandler(ValueNotAcceptedException.class)
        public ResponseEntity<?> handlerCpfAlreadyExistsException (ValueNotAcceptedException valueNotAcceptedException) {
            ValueNotAcceptedExceptionDetails exceptionDetails = ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder.newBuilder()
                    .details(valueNotAcceptedException.getMessage())
                    .build();
            return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
        }

    }


}
