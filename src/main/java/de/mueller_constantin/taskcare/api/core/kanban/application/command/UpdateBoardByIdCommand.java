package de.mueller_constantin.taskcare.api.core.kanban.application.command;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class UpdateBoardByIdCommand implements Command {
    @NotNull
    private UUID id;

    @NullOrNotEmpty
    @Size(max = 255)
    private String name;

    @NullOrNotEmpty
    @Size(max = 1024)
    private String description;

    private boolean descriptionTouched;

    public UpdateBoardByIdCommand(UUID id, String name, String description, boolean descriptionTouched) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.descriptionTouched = descriptionTouched;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionTouched = true;
    }
}
