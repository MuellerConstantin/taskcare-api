package de.x1c1b.taskcare.api.core.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controls filter settings when accessing a collection.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FilterSettings {

    private String filter;
}
