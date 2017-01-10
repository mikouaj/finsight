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

package pl.surreal.finance.transaction.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;

public class OldConfigMigrator
{
	private final static Logger LOGGER = LoggerFactory.getLogger(OldConfigMigrator.class);
	private HashMap<String,Label> labelCache = new HashMap<>();
	private OldConfig config;
	
	public OldConfigMigrator(OldConfig config) {
		this.config = config;
	}
	
	public List<Label> getLabels(boolean dependencies) {
		for(OldPatternConfig pattern : config.getPatterns()) {
			Label parent;
			if(labelCache.containsKey(pattern.getCategory())) {
				parent = labelCache.get(pattern.getCategory());
			} else {
				parent = new Label(pattern.getCategory());
				labelCache.put(parent.getText(),parent);
			}
			Label child;
			if(pattern.getSubcategory().equals(pattern.getCategory())) {
				LOGGER.warn("Skipping child label {} as parent with same text was found",pattern.getSubcategory());
				continue;
			}
			if(labelCache.containsKey(pattern.getSubcategory())) {
				child = labelCache.get(pattern.getSubcategory());
			} else {
				child = new Label(pattern.getSubcategory());
				labelCache.put(child.getText(),child);
			}
			if(dependencies) {
				parent.addChild(child);
			}
		}
		return new ArrayList<Label>(labelCache.values());
	}
	
	public List<LabelRule> getRules(boolean dependencies) {
		ArrayList<LabelRule> rules = new ArrayList<>();
		for(OldPatternConfig pattern : config.getPatterns()) {
			LabelRule rule = new LabelRule(pattern.getRegexp());
			rules.add(rule);
		}
		return rules;
	}
}
