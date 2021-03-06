/* Copyright 2017 Mikolaj Stefaniak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package pl.surreal.finance.transaction.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AuthDetailsApi {
    @JsonProperty
    @NotNull
    @NotEmpty
    private String userName;
    @JsonProperty
    @NotNull
    @NotEmpty
    private String userSecret;
    @JsonProperty
    @NotNull
    @NotEmpty
    private String appName;
    @JsonProperty
    @NotNull
    @NotEmpty
    private String appSecret;

    public AuthDetailsApi() {
    }

    public AuthDetailsApi(String userName, String userSecret, String appName, String appSecret) {
        this.userName = userName;
        this.userSecret = userSecret;
        this.appName = appName;
        this.appSecret = appSecret;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
