package de.mueller_constantin.taskcare.api.core.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for aggregates.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class Aggregate {
    private final UUID id;
    private int version;
    private boolean deleted;

    @JsonIgnore
    private final List<Event> uncommittedEvents = new ArrayList<>();

    protected Aggregate(UUID id, int version, boolean deleted) {
        this.id = id;
        this.version = version;
        this.deleted = deleted;
    }

    /**
     * Returns the next version of the aggregate. This is also the version that the next change
     * to the aggregate must have.
     *
     * @return The next version.
     */
    protected final int getNextVersion() {
        return version + 1;
    }

    /**
     * Applies the event to the aggregate.
     *
     * @param event The event to process.
     * @throws IllegalArgumentException If the event is unsupported.
     */
    protected abstract void processEvent(Event event) throws IllegalArgumentException;

    /**
     * Makes a change to the aggregate.
     *
     * <p>
     *     The version of the change must be consistent with the versioning/history of the aggregate.
     *     Ultimately, this means that the version of the event must be exactly one higher than the
     *     current version of the aggregate.
     * </p>
     *
     * @param event The event to process.
     * @throws IllegalArgumentException If the event cannot be applied or is unsupported.
     */
    protected final void applyChange(Event event) throws IllegalArgumentException {
        if(!id.equals(event.getAggregateId())) {
            throw new IllegalArgumentException("Event does not belong to this aggregate");
        }

        if (event.getVersion() != this.getNextVersion()) {
            throw new IllegalArgumentException(
                    "Inconsistent event history, expected version %d but got %d".formatted(
                            this.getNextVersion(), event.getVersion()));
        }

        processEvent(event);
        this.uncommittedEvents.add(event);
        this.version = event.getVersion();
    }

    /**
     * Reconstructs the aggregate from a list of events.
     *
     * <p>
     *     The events must be issued in a consistent order and for the aggregate.
     * </p>
     *
     * @param events The events to process.
     * @throws IllegalArgumentException If the event cannot be applied or is unsupported.
     */
    public final void loadFromHistory(List<Event> events) throws IllegalArgumentException {
        if(events == null || events.isEmpty()) {
            throw new IllegalArgumentException("Events must not be null or empty");
        }

        events.forEach((event) -> {
            if(!id.equals(event.getAggregateId())) {
                throw new IllegalArgumentException("Event does not belong to this aggregate");
            }

            if(event.getVersion() != this.getNextVersion()) {
                throw new IllegalArgumentException(
                        "Inconsistent event history, expected version %d but got %d".formatted(
                                this.getNextVersion(), event.getVersion()));
            }

            processEvent(event);
            this.version = event.getVersion();
        });
    }

    /**
     * Commits the changes of an aggregate. This means nothing other than that the changes made
     * have now been persisted.
     */
    public final void commit() {
        this.uncommittedEvents.clear();
    }

    /**
     * Returns the persistent version of the aggregate. This does not include versions of
     * changes that are not yet persistent.
     *
     * @return The persistent version.
     */
    public final int getCommittedVersion() {
        return this.version - this.uncommittedEvents.size();
    }

    /**
     * Handles the deletion of the aggregate. This method should also insert a corresponding event
     * into the change history of the aggregate.
     */
    protected abstract void processDelete();

    /**
     * Deletes an aggregate. Technically, the aggregate is only marked as deleted,
     * which means it is a soft delete.
     */
    public final void delete() {
        processDelete();
        this.deleted = true;
    }
}
