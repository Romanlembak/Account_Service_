package account.businesslayer.advice;

import com.fasterxml.jackson.annotation.*;

import java.net.URI;
import java.time.LocalDate;

public class ExceptionBodyResponse {
    private LocalDate timestamp;
    private int status;
    private String error;
    private String path;
    @JsonCreator
    public ExceptionBodyResponse(@JsonProperty String path) {
        this.timestamp = LocalDate.now();
        this.status = 400;
        this.error = "Bad Request";
        this.path = path;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }
}
