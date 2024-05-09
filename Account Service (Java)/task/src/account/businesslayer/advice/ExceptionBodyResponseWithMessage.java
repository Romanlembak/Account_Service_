package account.businesslayer.advice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ExceptionBodyResponseWithMessage extends ExceptionBodyResponse{
    private String message;
    @JsonCreator
    public ExceptionBodyResponseWithMessage(String path, @JsonProperty String message) {
        super(path);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }


}
