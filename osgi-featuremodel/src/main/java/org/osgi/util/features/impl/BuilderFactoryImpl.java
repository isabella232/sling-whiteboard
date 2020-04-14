/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.util.features.impl;

import org.osgi.util.features.ID;
import org.osgi.util.features.BuilderFactory;
import org.osgi.util.features.BundleBuilder;
import org.osgi.util.features.ConfigurationBuilder;
import org.osgi.util.features.ExtensionBuilder;
import org.osgi.util.features.FeatureBuilder;
import org.osgi.util.features.MergeContextBuilder;
import org.osgi.util.features.Extension.Kind;
import org.osgi.util.features.Extension.Type;

class BuilderFactoryImpl implements BuilderFactory {
    @Override
    public BundleBuilder newBundleBuilder(ID id) {
        return new BundleBuilderImpl(id);
    }

    @Override
    public ConfigurationBuilder newConfigurationBuilder(String pid) {
        return new ConfigurationBuilderImpl(pid);
    }

    @Override
    public ConfigurationBuilder newConfigurationBuilder(String factoryPid, String name) {
        return new ConfigurationBuilderImpl(factoryPid, name);
    }

    @Override
    public FeatureBuilder newFeatureBuilder(ID id) {
        return new FeatureBuilderImpl(id);
    }

    @Override
    public ExtensionBuilder newExtensionBuilder(String name, Type type, Kind kind) {
        return new ExtensionBuilderImpl(name, type, kind);
    }

    @Override
    public MergeContextBuilder newMergeContextBuilder() {
        return new MergeContextBuilderImpl();
    }
}
