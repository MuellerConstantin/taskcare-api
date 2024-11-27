package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private String id;
    private String username;
    private String role;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayName;
}
