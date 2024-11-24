-- ### Seed 'es_metadata' ###

-- # Aggregate - 065e84bd-2e41-418c-82df-886d7e0c6f72

INSERT INTO es_metadata (aggregate_id, aggregate_type, version, deleted)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate', 6, false);

-- # Aggregate - 09a47237-4c29-42fb-9dc7-3436fbe17da8

INSERT INTO es_metadata (aggregate_id, aggregate_type, version, deleted)
VALUES ('09a47237-4c29-42fb-9dc7-3436fbe17da8', 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate', 1, false);

-- # Aggregate - 09a47237-4c29-42fb-9dc7-3436fbe17da8

INSERT INTO es_metadata (aggregate_id, aggregate_type, version, deleted)
VALUES ('be828292-c982-4128-afe1-5b347cd61154', 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate', 3, true);

-- ### Seed 'es_events' ###

-- # Aggregate - 065e84bd-2e41-418c-82df-886d7e0c6f72

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 1, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 1, "timestamp": "2023-10-01T11:00:00+01:00", "property1": "value1", "property2": "value2"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 2, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 2, "timestamp": "2023-10-02T11:00:00+01:00", "property1": "update1", "property2": "update1"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 3, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 3, "timestamp": "2023-10-03T11:00:00+01:00", "property1": "update2", "property2": "update2"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 4, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 4, "timestamp": "2023-10-04T11:00:00+01:00", "property1": "update3", "property2": "update3"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 5, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 5, "timestamp": "2023-10-05T11:00:00+01:00", "property1": "update4", "property2": "update4"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 6, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 6, "timestamp": "2023-10-06T11:00:00+01:00", "property1": "update5", "property2": "update5"}');

-- # Aggregate - 09a47237-4c29-42fb-9dc7-3436fbe17da8

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('09a47237-4c29-42fb-9dc7-3436fbe17da8', 1, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent',
'{"aggregateId":"09a47237-4c29-42fb-9dc7-3436fbe17da8", "version": 1, "timestamp": "2023-10-01T11:00:00+01:00", "property1": "value1", "property2": "value2"}');

-- # Aggregate - 09a47237-4c29-42fb-9dc7-3436fbe17da8

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('be828292-c982-4128-afe1-5b347cd61154', 1, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent',
'{"aggregateId":"be828292-c982-4128-afe1-5b347cd61154", "version": 2, "timestamp": "2023-10-01T11:00:00+01:00", "property1": "value1", "property2": "value2"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('be828292-c982-4128-afe1-5b347cd61154', 2, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent',
'{"aggregateId":"be828292-c982-4128-afe1-5b347cd61154", "version": 3, "timestamp": "2023-10-01T11:00:00+01:00", "property1": "value1", "property2": "value2"}');

INSERT INTO es_events (aggregate_id, version, event_type, event_data)
VALUES ('be828292-c982-4128-afe1-5b347cd61154', 3, 'de.mueller_constantin.taskcare.api.core.dummy.domain.DummyDeletedEvent',
'{"aggregateId":"be828292-c982-4128-afe1-5b347cd61154", "version": 4, "timestamp": "2023-10-01T11:00:00+01:00"}');

-- ### Seed 'es_snapshots' ###

-- # Aggregate - 065e84bd-2e41-418c-82df-886d7e0c6f72

INSERT INTO es_snapshots (aggregate_id, version, aggregate_data)
VALUES ('065e84bd-2e41-418c-82df-886d7e0c6f72', 5,
'{"id":"065e84bd-2e41-418c-82df-886d7e0c6f72", "version": 5, "deleted": false, "property1": "update5", "property2": "update5"}');
