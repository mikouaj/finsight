/* Copyright 2016 Mikolaj Stefaniak
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

package pl.surreal.finance.transaction.parser;

import java.util.Optional;

import pl.surreal.finance.transaction.core.Transaction;

public interface ITransactionParser {
	public Optional<Transaction> getNext();
	public boolean hasNext();
	public void close();
//	public void addResourceLookup(Class<?> resourceClass, IResourceLookup<?> lookup);
//	public void removeResourceLookup(Class<?> resourceClass);
	public void setLabelProvider(ILabelProvider labelProvider);
}