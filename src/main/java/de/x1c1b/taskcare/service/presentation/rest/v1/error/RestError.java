package de.x1c1b.taskcare.service.presentation.rest.v1.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RestError {

    private String message;
    private int status;
    private OffsetDateTime timestamp;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RestErrorDetails> details;

    public static class RestErrorBuilder {

        public RestErrorBuilder detail(RestErrorDetails detail) {
            if (null == this.details) {
                this.details = new ArrayList<>();
            }

            this.details.add(detail);
            return this;
        }
    }
}
