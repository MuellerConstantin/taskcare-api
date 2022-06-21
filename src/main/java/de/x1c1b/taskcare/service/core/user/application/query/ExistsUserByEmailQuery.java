package de.x1c1b.taskcare.service.core.user.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExistsUserByEmailQuery {

    private String email;
}
