/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * with the License.  You may obtain a copy of the License at
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 */

package org.apache.sling.auth.saml2.sync;

import org.apache.jackrabbit.oak.spi.security.authentication.AbstractLoginModule;
import org.apache.jackrabbit.oak.spi.security.authentication.PreAuthenticatedLogin;
import org.apache.jackrabbit.oak.spi.security.authentication.external.*;
import org.apache.jackrabbit.oak.spi.whiteboard.Whiteboard;
import org.apache.jackrabbit.oak.spi.whiteboard.WhiteboardUtils;
import org.apache.sling.auth.saml2.impl.Saml2Credentials;
import org.apache.sling.auth.saml2.impl.Saml2ExternalIdentityProvider;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component(
        name = "SAML2 Login Module",
        service = Saml2LoginModule.class,
        immediate = true
)
public class Saml2LoginModule extends AbstractLoginModule {

    static Set<Class> SUPPORTED_CREDENTIALS = new HashSet<Class>();
    private static final int MAX_SYNC_ATTEMPTS = 3;
    private ExternalIdentityProvider externalIdentityProvider;
    private SyncHandler syncHandler;
    private ExternalUser externalUser;
    private final Logger logger = LoggerFactory.getLogger(Saml2LoginModule.class);

    static {
        SUPPORTED_CREDENTIALS.add(Saml2Credentials.class);
    }

    public Saml2LoginModule(){

    }

    @Nonnull
    @Override
    protected Set<Class> getSupportedCredentials() {
        return SUPPORTED_CREDENTIALS;
    }

    /**
     * Method to authenticate a {@code Subject} (phase 1).
     *
     * <p> The implementation of this method authenticates
     * a {@code Subject}.  For example, it may prompt for
     * {@code Subject} information such
     * as a username and password and then attempt to verify the password.
     * This method saves the result of the authentication attempt
     * as private state within the LoginModule.
     *
     * <p>
     *
     * @return true if the authentication succeeded, or false if this
     * {@code LoginModule} should be ignored.
     * @throws LoginException if the authentication fails
     */
    @Override
    public boolean login() throws LoginException {
        Credentials credentials = getCredentials();
        if (credentials instanceof Saml2Credentials) {
            final String userId = ((Saml2Credentials) credentials).getUserId();
            if (userId == null) {
                logger.warn("Could not extract userId from credentials");
            } else {
                sharedState.put(SHARED_KEY_PRE_AUTH_LOGIN, new PreAuthenticatedLogin(userId));
                sharedState.put(SHARED_KEY_CREDENTIALS, new SimpleCredentials(userId, new char[0]));
                sharedState.put(SHARED_KEY_LOGIN_NAME, userId);
                logger.info("Adding pre-authenticated login user '{}' to shared state.", userId);
            }
        }
        return false;
    }

    /**
     * Method to commit the authentication process (phase 2).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * {@code login} method), then this method associates relevant
     * Principals and Credentials with the {@code Subject} located in the
     * {@code LoginModule}.  If this LoginModule's own
     * authentication attempted failed, then this method removes/destroys
     * any state that was originally saved.
     *
     * <p>
     *
     * @return true if this method succeeded, or false if this
     * {@code LoginModule} should be ignored.
     * @throws LoginException if the commit fails
     */
    @Override
    public boolean commit() throws LoginException {
        return false;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        // 1. Get Whiteboard
        Whiteboard whiteboard = getWhiteboard();
        if (null == whiteboard) {
            logger.error("Header login module needs Whiteboard.");
            return;
        }
        logger.debug("Got Whiteboard");

        // 2. Get Identity Provider Manager
        ExternalIdentityProviderManager externalIdentityProviderManager = WhiteboardUtils.getService(whiteboard, ExternalIdentityProviderManager.class);
        if (null == externalIdentityProviderManager) {
            logger.error("SAML2 login module needs ExternalIdentityProviderManager.");
            return;
        }

        // 3. get Identity Provider
        externalIdentityProvider = externalIdentityProviderManager.getProvider(Saml2ExternalIdentityProvider.NAME);
        if (null == externalIdentityProvider) {
            logger.error("Header login module needs ExternalIdentityProvider. Can't get IDP with name: '{}'",
                    Saml2ExternalIdentityProvider.NAME);
            return;
        }

// 4. get Sync Manager
//        SyncManager syncManager = WhiteboardUtils.getService(whiteboard, SyncManager.class);
//        if (null == syncManager)
//        {
//            logger.error("Header login module needs SyncManager.");
//            return;
//        }

// 5. Get Sync Handler
//        String syncHandlerName = "default";
//        syncHandler = syncManager.getSyncHandler(syncHandlerName);
//        if (null == syncHandler)
//        {
//            logger.error("Header login module needs SyncHandler. Can't get SyncHandler: '{}'", syncHandlerName);
//            return;
//        }
    }

    @Override
    public boolean abort() throws LoginException
    {
        // TODO:  Do we need to override this method?
        logger.info("abort() called");
        return super.abort();
    }

    @Override
    protected void clearState()
    {
        // TODO:  Do we need to override this method?
        logger.info("clearState() called");
        super.clearState();
    }
}
