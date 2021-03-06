/*
 * Copyright © 2019 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
