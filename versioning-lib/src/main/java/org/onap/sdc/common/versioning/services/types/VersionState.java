package org.onap.sdc.common.versioning.services.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionState {
  private SynchronizationState synchronizationState;
  private boolean dirty;
}
