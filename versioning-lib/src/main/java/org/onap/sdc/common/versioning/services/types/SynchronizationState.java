package org.onap.sdc.common.versioning.services.types;

public enum SynchronizationState {
  UpToDate("Up to date"),
  OutOfSync("Out of sync"),
  Merging("Merging");

  private final String displayName;

  SynchronizationState(String displayName) {
    this.displayName = displayName;
  }

  public String toString() {
    return this.displayName;
  }
}
