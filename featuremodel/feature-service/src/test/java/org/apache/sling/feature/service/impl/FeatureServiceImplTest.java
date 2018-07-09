/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.feature.service.impl;

import org.apache.sling.feature.service.Features;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FeatureServiceImplTest {
    @Test
    public void testFeatureService() {
        Map<Long, String> bif = new HashMap<>();

        String f1 = "gid:aid:1.0.0:myfeature:slingfeature";
        bif.put(123L, f1);
        bif.put(456L, f1);

        String f2 = "gid:aid2:1.0.0";
        bif.put(789L, f2);

        Features fs = new FeatureServiceImpl(bif);
        assertEquals(2, fs.listFeatures().size());

        assertEquals(f1, fs.getFeatureForBundle(123));
        assertEquals(f1, fs.getFeatureForBundle(456));
        assertEquals(f2, fs.getFeatureForBundle(789));
        assertNull(fs.getFeatureForBundle(999));
    }
}
