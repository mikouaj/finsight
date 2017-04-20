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
import java.util.HashMap;

public class ApiSecurityConfiguration {
    @JsonProperty
    @NotNull
    private Long tokenLife;
    @JsonProperty
    @NotNull
    private TokenGeneratorAppsConfiguration[] tokenAllowedApps;

    public ApiSecurityConfiguration() {
    }

    public Long getTokenLife() {
        return tokenLife;
    }

    public void setTokenLife(Long tokenLife) {
        this.tokenLife = tokenLife;
    }

    public TokenGeneratorAppsConfiguration[] getTokenAllowedApps() {
        return tokenAllowedApps;
    }

    public void setTokenAllowedApps(TokenGeneratorAppsConfiguration[] tokenAllowedApps) {
        this.tokenAllowedApps = tokenAllowedApps;
    }

    public HashMap<String,String> getTokenAllowedAppsMap() {
        HashMap<String,String> hashMap = new HashMap<>();
        for(TokenGeneratorAppsConfiguration appConfig : tokenAllowedApps) {
            hashMap.put(appConfig.getName(), appConfig.getSecret());
        }
        return hashMap;
    }
}