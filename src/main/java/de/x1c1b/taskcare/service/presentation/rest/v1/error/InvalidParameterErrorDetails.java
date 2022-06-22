package de.x1c1b.taskcare.service.presentation.rest.v1.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvalidParameterErrorDetails implements RestErrorDetails {

    private String parameter;
    private String message;
}
