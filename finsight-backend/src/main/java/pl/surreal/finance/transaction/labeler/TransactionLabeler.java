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

package pl.surreal.finance.transaction.labeler;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.surreal.finance.transaction.core.CardOperation;
import pl.surreal.finance.transaction.core.Label;
import pl.surreal.finance.transaction.core.LabelRule;
import pl.surreal.finance.transaction.core.Transaction;
import pl.surreal.finance.transaction.core.Transfer;
import pl.surreal.finance.transaction.db.LabelRuleDAO;

public class TransactionLabeler implements ITransactionLabeler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLabeler.class);
	private final LabelRuleDAO labelRuleDAO;
	
	public TransactionLabeler(LabelRuleDAO labelRuleDAO) {
		this.labelRuleDAO = labelRuleDAO;
	}

	private List<Matcher> getTransferMatcher(Transfer transfer,Pattern p) {
		List<Matcher> matchers = new ArrayList<>();
		matchers.add(p.matcher(transfer.getDescription()));
		return matchers;
	}
	
	private List<Matcher> getCardOperationMatcher(CardOperation cardop,Pattern p) {
		List<Matcher> matchers = new ArrayList<>();
		matchers.add(p.matcher(cardop.getDestination()));
		return matchers;
	}
	
	private boolean anyMatch(List<Matcher> matchers) {
		for(Matcher m : matchers) {
			if(m.matches()) return true;
		}
		return false;
	}
	
	public List<Label> getLabels(Transaction t,LabelRule labelRule) {
		List<Label> labels = new ArrayList<Label>();
		if(labelRule.isActive()) {
			try {
				Pattern pattern = Pattern.compile(labelRule.getRegexp());
				List<Matcher> matchers;
				if(t instanceof CardOperation) {
					matchers = getCardOperationMatcher((CardOperation)t,pattern);
				} else if(t instanceof Transfer) {
					matchers = getTransferMatcher((Transfer)t,pattern);
				} else {
					matchers = null;
				}
				if(matchers!=null && anyMatch(matchers)) {
					labels.addAll(labelRule.getLabels());
				}
			} catch(PatternSyntaxException ex) {
				LOGGER.warn("getLabel : labelRule '{}' pattern compilation error '{}'",labelRule.getId(),ex.getMessage());
			}
		}
		return labels;
	}
	
	public List<Label> getLabels(Transaction t) {
		List<Label> labels = new ArrayList<Label>();
		for(LabelRule labelRule : labelRuleDAO.findAll()) {
			labels.addAll(getLabels(t,labelRule));
		}
		return labels;
	}

	@Override
	public int label(Transaction t) {
		List<Label> labels = getLabels(t);
		int appliedCount = 0;
	    for(Label label : labels) {
	    	if(!t.getLabels().contains(label)) {
	    		t.getLabels().add(label);
	    		appliedCount++;
	    	}
	    }
		return appliedCount;
	}

	@Override
	public int label(Transaction t, Long ruleId) throws NoSuchElementException {
		LabelRule labelRule = labelRuleDAO.findById(ruleId).orElseThrow(() -> new NoSuchElementException("LabelRule not found."));
		List<Label> labels = getLabels(t,labelRule);
		int appliedCount = 0;
	    for(Label label : labels) {
	    	if(!t.getLabels().contains(label)) {
	    		t.getLabels().add(label);
	    		appliedCount++;
	    	}
	    }
		return appliedCount;
	}
}
