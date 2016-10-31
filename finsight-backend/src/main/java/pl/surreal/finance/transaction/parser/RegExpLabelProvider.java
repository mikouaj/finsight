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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.db.LabelRuleDAO;

public class RegExpLabelProvider implements ILabelProvider
{
	private final static Logger LOGGER = LoggerFactory.getLogger(RegExpLabelProvider.class);
	private LabelRuleDAO labelRuleDAO;
	
	public RegExpLabelProvider(LabelRuleDAO labelRuleDAO) {
		this.labelRuleDAO = labelRuleDAO;
	}

	@Override
	public List<Label> getLabels(String description) {
		List<Label> labels = new ArrayList<>();
		List<LabelRule> labelRules = labelRuleDAO.findAll();
		for(LabelRule rule : labelRules) {
			String regExp = rule.getRegexp();
			try { 
			 Pattern pattern = Pattern.compile(regExp);
			 Matcher matcher = pattern.matcher(description);
			 if(matcher.matches()) {
				 labels.addAll(rule.getLabels());
			 }
			} catch(PatternSyntaxException ex) {
				LOGGER.warn("getLabels : pattern '{}' has wrong syntax, exception '{}'",regExp,ex.getMessage());
				continue;
			}
		}
		return labels;
	}
}
