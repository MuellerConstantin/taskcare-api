package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class UserDto {
    @Searchable
    private String id;

    @Searchable
    private String username;

    @Searchable
    @JsonView({DefaultJsonViews.Administrator.class, DefaultJsonViews.Me.class})
    private String role;

    @Searchable
    @JsonView(DefaultJsonViews.Administrator.class)
    private String identityProvider;

    @Searchable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayName;
}
