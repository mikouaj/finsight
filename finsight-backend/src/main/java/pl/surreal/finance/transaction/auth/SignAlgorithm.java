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

package pl.surreal.finance.transaction.auth;

import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public enum SignAlgorithm {
    HMAC256(SignAlgorithmType.SECRET),
    HMAC512(SignAlgorithmType.SECRET),
    RSA256(SignAlgorithmType.KEY),
    RSA512(SignAlgorithmType.KEY);

    private static final Logger LOGGER = LoggerFactory.getLogger(SignAlgorithm.class);
    public enum SignAlgorithmType { SECRET, KEY };
    private final SignAlgorithmType type;

    SignAlgorithm(SignAlgorithmType type) {
        this.type = type;
    }

    public SignAlgorithmType getType() {
        return type;
    }

    public static Algorithm getSecretAlgorithm(SignAlgorithm algCode, String secret) throws Exception {
        switch(algCode) {
            case HMAC256:
                return Algorithm.HMAC256(secret);
            case HMAC512:
                return Algorithm.HMAC512(secret);
        }
        throw new Exception("getSecretAlgorithm unsupported algorithm");
    }

    public static Algorithm getKeyAlgorithm(SignAlgorithm algCode, String pubKey, String privKey) throws Exception {
        throw new Exception("getKeyAlgorithm unsupported algorithm");
    }

    public static Optional<Algorithm> getAlgorithm(String algCode,String...signData) {
        Algorithm algorithm=null;
        try {
            SignAlgorithm signAlgorithm = Enum.valueOf(SignAlgorithm.class,algCode);
            switch(signData.length) {
                case 1:
                    if(signAlgorithm.type== SignAlgorithmType.SECRET) {
                        algorithm = getSecretAlgorithm(signAlgorithm,signData[0]);
                    }
                    if(signAlgorithm.type== SignAlgorithmType.KEY) {
                        algorithm = getKeyAlgorithm(signAlgorithm,signData[0],null);
                    }
                    break;
                case 2:
                    if(signAlgorithm.type== SignAlgorithmType.KEY) {
                        algorithm = getKeyAlgorithm(signAlgorithm,signData[0],signData[1]);
                    }
                    break;
                default:
                    throw new Exception("Illegal arguments for selected algorithm");
            }
        } catch(Exception e) {
            LOGGER.warn("getAlgorithm exception due to '{}'",e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(algorithm);
    }
}
