/*
 * File created on Feb 22, 2019
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

import static org.soulwing.jwt.extension.ExtensionLogger.LOGGER;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.function.Supplier;

import org.jboss.as.controller.services.path.PathManager;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.crypto.PublicKeyFormat;
import org.soulwing.jwt.crypto.PublicKeyLoader;
import org.soulwing.jwt.crypto.PublicKeyType;

/**
 * A service for a public key component.

 * @author Carl Harris
 */
class PublicKeyService implements Service<PublicKeyService> {

  private final String kid;
  private final PublicKeyType type;
  private final PublicKeyFormat format;
  private final String path;
  private final String relativeTo;

  private Supplier<ProfileService> profileService;
  private Supplier<PathManager> pathManager;

  private PathManager.Callback.Handle callbackHandle;
  private Path resolvedPath;

  public PublicKeyService(String kid, PublicKeyType type,
      PublicKeyFormat format, String path, String relativeTo) {
    this.kid = kid;
    this.type = type;
    this.format = format;
    this.path = path;
    this.relativeTo = relativeTo;
  }

  void setProfileService(Supplier<ProfileService> profileService) {
    this.profileService = profileService;
  }

  void setPathManager(Supplier<PathManager> pathManager) {
    this.pathManager = pathManager;
  }

  @Override
  public PublicKeyService getValue() throws IllegalStateException,
      IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {

    if (relativeTo != null) {
      resolvedPath = Paths.get(
          pathManager.get().resolveRelativePathEntry(path, relativeTo));
      callbackHandle = pathManager.get().registerCallback(
          relativeTo, PathManager.ReloadServerCallback.create(),
          PathManager.Event.UPDATED, PathManager.Event.REMOVED);
    }
    else {
      resolvedPath = Paths.get(path);
    }

    profileService.get().addPublicKeyService(kid, this);
  }

  @Override
  public void stop(StopContext stopContext) {

    if (callbackHandle != null) {
      callbackHandle.remove();
    }

    profileService.get().removePublicKeyService(kid, this);
  }

  public PublicKey getPublicKey()
      throws IllegalArgumentException, InvalidKeySpecException, IOException {
    LOGGER.info("request for key " + kid + " from path " + resolvedPath);
    return PublicKeyLoader.loadKey(resolvedPath, format, type);
  }

}
