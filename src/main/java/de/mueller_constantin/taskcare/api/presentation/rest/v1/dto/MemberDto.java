package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.Searchable;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view.DefaultJsonViews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonView(DefaultJsonViews.User.class)
public class MemberDto {
    @Searchable
    private String id;

    @Searchable
    private String userId;

    @Searchable
    private String role;
}
