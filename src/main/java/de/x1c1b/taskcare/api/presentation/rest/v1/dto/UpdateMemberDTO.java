package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateMemberDTO {

    private String role;
}
