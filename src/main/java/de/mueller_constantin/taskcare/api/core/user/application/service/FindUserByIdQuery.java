package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindUserByIdQuery implements Query {
    private UUID id;
}
