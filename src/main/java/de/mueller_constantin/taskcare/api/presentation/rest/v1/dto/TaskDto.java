package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.Searchable;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view.DefaultJsonViews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonView(DefaultJsonViews.User.class)
public class TaskDto {
    private String id;
    private String boardId;
    private String name;
    private String description;
    private String statusId;
    private String statusUpdatedAt;
    private String assigneeId;
    private String createdAt;
    private List<String> componentIds;
    private String dueDate;
    private String priority;
}
