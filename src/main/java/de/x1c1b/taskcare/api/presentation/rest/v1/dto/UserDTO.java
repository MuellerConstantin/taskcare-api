package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private String username;
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
}
