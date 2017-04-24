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

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public final class AuthUtils {
    public static Optional<Algorithm> getAlgorithm(String algCode, String secret) {
        Algorithm algorithm = null;
        try {
            switch (algCode) {
                case "HMAC256":
                    algorithm = Algorithm.HMAC256(secret);
                    break;
                case "HMAC512":
                    algorithm = Algorithm.HMAC512(secret);
                    break;
            }
        } catch(UnsupportedEncodingException e) {
            //LOGGER.warn("getAlgorithm exception due to '{}'",e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(algorithm);
    }
}