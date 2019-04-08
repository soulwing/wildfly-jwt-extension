/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.undertow;

import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.Credential;
import io.undertow.util.AttachmentKey;

/**
 * Attachment keys used by JWT.
 *
 * @author Carl Harris
 */
interface JwtAttachments {

  AttachmentKey<Credential> CREDENTIAL_KEY =
      AttachmentKey.create(Credential.class);

  AttachmentKey<Integer> AUTH_FAILED_KEY =
      AttachmentKey.create(Integer.class);

  AttachmentKey<String> AUTH_MESSAGE_KEY =
      AttachmentKey.create(String.class);
  
  AttachmentKey<Authenticator> AUTHENTICATOR_KEY =
      AttachmentKey.create(Authenticator.class);

}
