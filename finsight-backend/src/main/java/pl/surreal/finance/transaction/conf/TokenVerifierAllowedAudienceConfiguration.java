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
package pl.surreal.finance.transaction.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class TokenVerifierAllowedAudienceConfiguration {
    @JsonProperty
    @NotNull
    private String name;
    @JsonProperty
    @NotNull
    private TokenConfiguration[] authTokens;

    public TokenVerifierAllowedAudienceConfiguration() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TokenConfiguration[] getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(TokenConfiguration[] authTokens) {
        this.authTokens = authTokens;
    }
}
