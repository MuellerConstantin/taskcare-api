package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonView;
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
public class ComponentDto {
    private String id;
    private String name;
    private String description;
}
