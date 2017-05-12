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

import java.io.UnsupportedEncodingException;

import com.auth0.jwt.algorithms.Algorithm;

public enum JWTSignAlgorithm {
    HMAC256(SignAlgorithmType.SECRET),
    HMAC512(SignAlgorithmType.SECRET),
    RSA256(SignAlgorithmType.KEY),
    RSA512(SignAlgorithmType.KEY);

    //private static final Logger LOGGER = LoggerFactory.getLogger(JWTSignAlgorithm.class);
    public enum SignAlgorithmType { SECRET, KEY };
    private final SignAlgorithmType type;

    JWTSignAlgorithm(SignAlgorithmType type) {
        this.type = type;
    }

    public SignAlgorithmType getType() {
        return type;
    }

	public static Algorithm getSecretAlgorithm(JWTSignAlgorithm algCode, String secret) throws UnsupportedEncodingException,IllegalArgumentException {
    	switch(algCode) {
            case HMAC256:
            	return Algorithm.HMAC256(secret);
            case HMAC512:
            	return Algorithm.HMAC512(secret);
        	default:
            	throw new IllegalArgumentException("Provided algorithm not supported");
        }
    }

    public static Algorithm getKeyAlgorithm(JWTSignAlgorithm algCode, String pubKey, String privKey) throws UnsupportedEncodingException,IllegalArgumentException {
    	throw new IllegalArgumentException("Provided algorithm not supported");
    }
}
