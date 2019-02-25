/*
 * File created on Feb 19, 2019
 *
 * Copyright (c) 2019 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.jwt.extension;

import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;

/**
 * Resource path constants.
 *
 * @author Carl Harris
 */
public interface Paths {

  PathElement SUBSYSTEM  =
      PathElement.pathElement(ModelDescriptionConstants.SUBSYSTEM,
          Names.SUBSYSTEM_NAME);
  
  PathElement PROFILE = PathElement.pathElement(Names.PROFILE);
  
  PathElement CLAIM_ASSERTION = PathElement.pathElement(
      Names.CLAIM_ASSERTION);

  PathElement SECRET_KEY = PathElement.pathElement(Names.SECRET_KEY);

  PathElement PUBLIC_KEY = PathElement.pathElement(Names.PUBLIC_KEY);

  PathElement PREDICATE = PathElement.pathElement(Names.PREDICATE);

  PathElement CLAIM_TRANSFORM = PathElement.pathElement(
      Names.CLAIM_TRANSFORM);

  PathElement TRANSFORMER =
      PathElement.pathElement(Names.TRANSFORMER);

}
