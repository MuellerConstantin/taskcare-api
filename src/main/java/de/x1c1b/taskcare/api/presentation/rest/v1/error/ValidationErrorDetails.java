package de.x1c1b.taskcare.api.presentation.rest.v1.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ValidationErrorDetails implements RestErrorDetails {

    private String field;
    private String message;
}
