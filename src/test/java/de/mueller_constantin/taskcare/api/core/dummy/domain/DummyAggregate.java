package de.mueller_constantin.taskcare.api.core.dummy.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DummyAggregate extends Aggregate {
    private String property1;
    private String property2;

    public DummyAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public DummyAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(Event event) {
        if (event instanceof DummyCreatedEvent) {
            this.setProperty1(((DummyCreatedEvent) event).getProperty1());
            this.setProperty2(((DummyCreatedEvent) event).getProperty2());
            return;
        } else if (event instanceof DummyUpdatedEvent) {
            this.setProperty1(((DummyUpdatedEvent) event).getProperty1());
            this.setProperty2(((DummyUpdatedEvent) event).getProperty2());
            return;
        } else if (event instanceof DummyDeletedEvent) {
            return;
        }

        throw new IllegalArgumentException("Unknown event type");
    }

    private void setProperty1(String property1) {
        this.property1 = property1;
    }

    private void setProperty2(String property2) {
        this.property2 = property2;
    }

    @Override
    protected void processDelete() {
        this.applyChange(DummyDeletedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build());
    }

    public void create(String property1, String property2) {
        this.applyChange(DummyCreatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .property1(property1)
                .property2(property2)
                .build()
        );
    }

    public void update(String property1, String property2) {
        this.applyChange(DummyUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .property1(property1)
                .property2(property2)
                .build()
        );
    }
}
