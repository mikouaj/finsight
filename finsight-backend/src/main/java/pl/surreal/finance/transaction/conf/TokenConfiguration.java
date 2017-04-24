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

public class TokenConfiguration {
    @JsonProperty
    @NotNull
    private String issuer;
    @JsonProperty
    @NotNull
    private String type;
    @JsonProperty
    @NotNull
    private String signAlgorithm;
    @JsonProperty
    private String signSecret;
    @JsonProperty
    private String[] signPemKeys;

    public TokenConfiguration() {
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getSignSecret() {
        return signSecret;
    }

    public void setSignSecret(String signSecret) {
        this.signSecret = signSecret;
    }

    public String[] getSignPemKeys() {
        return signPemKeys;
    }

    public void setSignPemKeys(String[] signPemKeys) {
        this.signPemKeys = signPemKeys;
    }
}
