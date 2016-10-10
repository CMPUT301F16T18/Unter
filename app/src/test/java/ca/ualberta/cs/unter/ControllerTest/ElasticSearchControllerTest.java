/*
 * Copyright (C) 2016 CMPUT301F16T18 - Alan(Xutong) Zhao, Michael(Zichun) Lin, Stephen Larsen, Yu Zhu, Zhenzhe Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ca.ualberta.cs.unter.ControllerTest;

import junit.framework.TestCase;

import ca.ualberta.cs.unter.controller.ElasticSearchController;
import ca.ualberta.cs.unter.model.Rider;

/**
 * The type Elastic search controller test.
 */
public class ElasticSearchControllerTest extends TestCase{
    /**
     * The Esc.
     */
    ElasticSearchController esc = new ElasticSearchController();
    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Test show user profile.
     */
    public void testShowUserProfile() {
        assertTrue(esc.findUserByUserName("xxx") != null);
    }

    /**
     * Test edit user profile.
     */
    public void testEditUserProfile() {
        Rider rider = new Rider("test", "7807163939", "test@ualberta.ca");
        assertEquals(esc.updateUser(rider), 1);
    }
}
