package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private boolean firstNameDirty;

    @JsonIgnore
    private boolean lastNameDirty;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.firstNameDirty = true;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.lastNameDirty = true;
    }
}
