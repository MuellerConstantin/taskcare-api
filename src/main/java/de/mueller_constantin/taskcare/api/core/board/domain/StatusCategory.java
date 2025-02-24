package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

/**
 * These are the categories used by status. Based on this categories different
 * workflow rules can be defined.
 */
public enum StatusCategory implements ValueObject {
    TO_DO,
    IN_PROGRESS,
    DONE
}
