/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.rar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Sets;
import org.wso2.carbon.identity.oauth2.rar.model.AuthorizationDetail;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.wso2.carbon.identity.oauth2.rar.util.AuthorizationDetailsConstants.EMPTY_JSON_ARRAY;
import static org.wso2.carbon.identity.oauth2.rar.util.AuthorizationDetailsConstants.EMPTY_JSON_OBJECT;
import static org.wso2.carbon.identity.oauth2.rar.util.TestConstants.TEST_NAME;
import static org.wso2.carbon.identity.oauth2.rar.util.TestConstants.TEST_TYPE;

/**
 * Test class for {@link AuthorizationDetailsCommonUtils}.
 */
public class AuthorizationDetailsCommonUtilsTest {

    private ObjectMapper objectMapper;
    private ObjectMapper mockObjectMapper;

    @BeforeClass
    public void setUp() throws JsonProcessingException {

        this.objectMapper = AuthorizationDetailsCommonUtils.getDefaultObjectMapper();
        this.mockObjectMapper = Mockito.spy(this.objectMapper);

        // mock
        doThrow(JsonProcessingException.class)
                .when(this.mockObjectMapper).writeValueAsString(any(TestDAOUtils.TestAuthorizationDetail.class));
        doThrow(JsonProcessingException.class).when(this.mockObjectMapper).writeValueAsString(any(Set.class));
    }

    @DataProvider(name = "AuthorizationDetailsCommonUtilsTestDataProvider")
    public Object[][] provideAuthorizationDetailsCommonUtilsTestData(Method testMethod) {

        switch (testMethod.getName()) {
            case "shouldReturnNull_whenJSONIsInvalid":
            case "shouldReturnCorrectSize_whenJSONArrayIsValid":
                return new Object[][]{
                        {null, 0},
                        {"", 0},
                        {" ", 0},
                        {"invalid JSON", 0},
                        {"[]", 0},
                        {"[{}]", 1},
                        {"[{},{}]", 2}
                };
            case "shouldReturnCorrectType_whenJSONIsValid":
                return new Object[][]{
                        {AuthorizationDetail.class},
                        {TestDAOUtils.TestAuthorizationDetail.class}
                };
        }
        return null;
    }

    @Test(dataProvider = "AuthorizationDetailsCommonUtilsTestDataProvider")
    public void shouldReturnCorrectSize_whenJSONArrayIsValid(String inputJson, int expectedSize) {

        Set<AuthorizationDetail> actualAuthorizationDetails = AuthorizationDetailsCommonUtils
                .fromJSONArray(inputJson, AuthorizationDetail.class, objectMapper);

        assertEquals(expectedSize, actualAuthorizationDetails.size());
    }

    @Test(dataProvider = "AuthorizationDetailsCommonUtilsTestDataProvider")
    public void shouldReturnNull_whenJSONIsInvalid(String inputJson, int expectedSize) {

        assertNull(AuthorizationDetailsCommonUtils.fromJSON(inputJson, AuthorizationDetail.class, objectMapper));
    }

    @Test(dataProvider = "AuthorizationDetailsCommonUtilsTestDataProvider")
    public <T extends AuthorizationDetail> void shouldReturnCorrectType_whenJSONIsValid(Class<T> expectedClazz) {

        final String inputJson = "{\"type\": \"" + TEST_TYPE + "\"}";
        AuthorizationDetail actualAuthorizationDetail =
                AuthorizationDetailsCommonUtils.fromJSON(inputJson, expectedClazz, objectMapper);

        assertNotNull(actualAuthorizationDetail);
        assertEquals(TEST_TYPE, actualAuthorizationDetail.getType());
    }

    @Test
    public void shouldReturnCorrectJson_whenAuthorizationDetailsAreValid() {

        TestDAOUtils.TestAuthorizationDetail inputAuthorizationDetail = new TestDAOUtils.TestAuthorizationDetail();
        inputAuthorizationDetail.setType(TEST_TYPE);
        inputAuthorizationDetail.setName(TEST_NAME);

        final String authorizationDetails = AuthorizationDetailsCommonUtils
                .toJSON(Sets.newHashSet(inputAuthorizationDetail), objectMapper);

        assertTrue(authorizationDetails.contains(TEST_TYPE));
        assertTrue(authorizationDetails.contains(TEST_NAME));
        assertEquals(EMPTY_JSON_ARRAY,
                AuthorizationDetailsCommonUtils.toJSON((Set<AuthorizationDetail>) null, objectMapper));
        assertEquals(EMPTY_JSON_ARRAY,
                AuthorizationDetailsCommonUtils.toJSON(Sets.newHashSet(inputAuthorizationDetail), mockObjectMapper));
    }

    @Test
    public void shouldReturnCorrectJson_whenAuthorizationDetailIsValid() {

        TestDAOUtils.TestAuthorizationDetail inputAuthorizationDetail = new TestDAOUtils.TestAuthorizationDetail();
        inputAuthorizationDetail.setType(TEST_TYPE);
        inputAuthorizationDetail.setName(TEST_NAME);

        final String authorizationDetail =
                AuthorizationDetailsCommonUtils.toJSON(inputAuthorizationDetail, objectMapper);

        assertTrue(authorizationDetail.contains(TEST_TYPE));
        assertTrue(authorizationDetail.contains(TEST_NAME));
        assertEquals(EMPTY_JSON_OBJECT,
                AuthorizationDetailsCommonUtils.toJSON((TestDAOUtils.TestAuthorizationDetail) null, objectMapper));
        assertEquals(EMPTY_JSON_OBJECT,
                AuthorizationDetailsCommonUtils.toJSON(new TestDAOUtils.TestAuthorizationDetail(), mockObjectMapper));
    }

    @Test
    public void shouldReturnMap_whenAuthorizationDetailIsValid() {

        TestDAOUtils.TestAuthorizationDetail inputAuthorizationDetail = new TestDAOUtils.TestAuthorizationDetail();
        inputAuthorizationDetail.setType(TEST_TYPE);
        inputAuthorizationDetail.setName(TEST_NAME);
        Map<String, Object> map = AuthorizationDetailsCommonUtils.toMap(inputAuthorizationDetail, objectMapper);

        assertTrue(map.containsKey("type"));
        assertTrue(map.containsKey("name"));
        assertEquals(TEST_TYPE, String.valueOf(map.get("type")));
        assertEquals(TEST_NAME, String.valueOf(map.get("name")));
        assertEquals(2, map.keySet().size());

        assertFalse(AuthorizationDetailsCommonUtils.toMap(null, objectMapper).containsKey(TEST_TYPE));
    }
}
