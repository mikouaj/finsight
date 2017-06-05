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

package pl.surreal.finance.transaction.auth.token;

public interface ITokenDecoder<C> {
    public boolean isValid(C token);
    public String getIssuer(C token) throws TokenDecoderException;
    public String getSubject(C token) throws TokenDecoderException;
}