/**
 * Copyright (C) 2010 Technische Universität Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.xmpp.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Benjamin Söllner
 */
public class ConditionInfo implements XMPPInfo, Cloneable {

	private static final long serialVersionUID = 1L;
	public static final String CHILD_ELEMENT = "condition";
	public static final String NAMESPACE = "http://www.rn.inf.tu-dresden.de/mobilis";
	public static final int OP_EQ = 0;
	public static final int OP_NE = 1;
	public static final int OP_GT = 2;
	public static final int OP_LT = 3;
	public static final int OP_GE = 4;
	public static final int OP_LE = 5;
	public static final int OP_AND = 6;
	public static final int OP_OR  = 7;
	public static final int OP_NOT = 8; 

	protected String key = null;
	protected int op = ConditionInfo.OP_EQ;
	protected String value = null;
	protected List<ConditionInfo> conditions = new LinkedList<ConditionInfo>();

	public String getKey() { return this.key; }
	public int getOp() { return this.op; }
	public String getValue() { return this.value; }
	public List<ConditionInfo> getConditions() { return this.conditions; }
	
	public void setKey(String key) { this.key = key; }
	public void setOp(int op) { this.op = op; }
	public void setValue(String value) { this.value = value; }
	
	public ConditionInfo() {}
	public ConditionInfo(int op) { this.op = op; }
	public ConditionInfo(String key, int op, String value) {
		this.key = key; this.op = op; this.value = value;
	}
	
	@Override
	public ConditionInfo clone() {
		ConditionInfo twin = new ConditionInfo(this.key, this.op, this.value);
		twin.conditions.clear();
		for (ConditionInfo c: this.conditions)
			twin.conditions.add(c.clone());
		return twin;
	}
	
	@Override
	public void fromXML(XmlPullParser parser) throws Exception {
		String ce = ConditionInfo.CHILD_ELEMENT;
		boolean first = true, inside = false, done = false;
		this.conditions.clear();
		do {		
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				if (parser.getName().equals(ce) && !first) {
					ConditionInfo c = new ConditionInfo();
					c.fromXML(parser);
					inside = true;
					this.conditions.add(c);
				} else if (parser.getName().equals(ce) && first) {
					int n = parser.getAttributeCount();
					for (int i = 0; i < n; i++) {
						String attributeName = parser.getAttributeName(i);
						String attributeValue = parser.getAttributeValue(i);
						if (attributeName.equals("key"))
							this.key = attributeValue;
						else if (attributeName.equals("op")) {
							if (attributeValue.equals("eq"))        this.op = ConditionInfo.OP_EQ;
							else if (attributeValue.equals("ne"))   this.op = ConditionInfo.OP_NE;
							else if (attributeValue.equals("gt"))   this.op = ConditionInfo.OP_GT;
							else if (attributeValue.equals("lt"))   this.op = ConditionInfo.OP_LT;
							else if (attributeValue.equals("ge"))   this.op = ConditionInfo.OP_GE;
							else if (attributeValue.equals("le"))   this.op = ConditionInfo.OP_LE;
							else if (attributeValue.equals("and"))  this.op = ConditionInfo.OP_AND;
							else if (attributeValue.equals("or"))   this.op = ConditionInfo.OP_OR;
							else if (attributeValue.equals("not"))  this.op = ConditionInfo.OP_NOT;
						} else if (attributeName.equals("value"))
							this.value = attributeValue;
					}
					first = false;
					parser.next();
				} else
					parser.next();
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals(ce) && inside && !first) {
					inside = false;
					parser.next();
				} else if (parser.getName().equals(ce) && !inside)
					done = true;
				else 
					parser.next();
				break;
			case XmlPullParser.END_DOCUMENT:
				done = true;
				break;
			default:
				parser.next();
			}
		} while (!done);
	}

	@Override
	public String toXML() {
		String ce = ConditionInfo.CHILD_ELEMENT;
		String op;
		switch (this.op) {
			case ConditionInfo.OP_NOT:         op = "xor";  break;
			case ConditionInfo.OP_OR:          op = "or";   break;
			case ConditionInfo.OP_AND:         op = "and";  break;
			case ConditionInfo.OP_LE:          op = "le";   break;
			case ConditionInfo.OP_GE:          op = "ge";   break;
			case ConditionInfo.OP_LT:          op = "lt";   break;
			case ConditionInfo.OP_GT:          op = "gt";   break;
			case ConditionInfo.OP_NE:          op = "ne";   break;
			case ConditionInfo.OP_EQ: default: op = "eq";   break;
		}
		StringBuilder sb = new StringBuilder()
				.append("<").append(ce)
				.append(" op=\"").append(op).append("\"");
		if (this.key != null)
			sb.append(" key=\"").append(this.key).append("\"");
		if (this.value != null)
			sb.append(" value=\"").append(this.value).append("\"");
		sb.append(">");
		for (ConditionInfo t: this.conditions)
			sb.append(t.toXML());
		sb.append("</").append(ce).append(">");
		return sb.toString();
	}

	@Override
	public String getChildElement() {
		return ConditionInfo.CHILD_ELEMENT;
	}

	@Override
	public String getNamespace() {
		return ConditionInfo.NAMESPACE;
	}
	
	
	public String toHQL(final Map<String, String> keyLookup) {
		int op = this.getOp();
		String key = this.getKey();
		String value = this.getValue();
		String subHQL = "";
		switch (op) {
		case ConditionInfo.OP_NOT:
			if (this.getConditions().size() == 0)
				return null;
			else {
				ConditionInfo subterm = this.getConditions().get(0);
				subHQL = subterm.toHQL(keyLookup);
				if (subHQL != null)
					return "not ("+subHQL+")";
				else return null;
			}
		case ConditionInfo.OP_AND: case ConditionInfo.OP_OR:
			for (ConditionInfo subterm: this.getConditions()) {
				String s = subterm.toHQL(keyLookup);
				if (s != null) {
					if (subHQL != "")
						subHQL += (op == ConditionInfo.OP_AND ? " and " : " or ");
					subHQL += "(" + s + ")";
				}
					
				else return null;
			}
			return subHQL;
		case ConditionInfo.OP_EQ: case ConditionInfo.OP_NE: case ConditionInfo.OP_GE: case ConditionInfo.OP_GT: case ConditionInfo.OP_LE: case ConditionInfo.OP_LT:
			if (keyLookup.containsKey(key))
				subHQL = keyLookup.get(key);
			else
				return null;
			switch (op) {
				case ConditionInfo.OP_EQ: subHQL += "=";  break;
				case ConditionInfo.OP_NE: subHQL += "!="; break;
				case ConditionInfo.OP_GT: subHQL += ">="; break;
				case ConditionInfo.OP_LT: subHQL += "<="; break;
				case ConditionInfo.OP_GE: subHQL += ">="; break;
				case ConditionInfo.OP_LE: subHQL += "<="; break;
				default: return null;
			}
			return subHQL + "'"+value.replace("'", "\\'")+"'"; 
		default:
			return null;
		}
	}

	
	public boolean getKeys(Set<String> target) {
		switch (this.op) {
		case ConditionInfo.OP_NOT:
			if (this.getConditions().size() == 0
					|| !this.getConditions().get(0).getKeys(target))
				return false;
			else
				return true;
		case ConditionInfo.OP_AND: case ConditionInfo.OP_OR:
			for (ConditionInfo subterm: this.getConditions())
				if (!subterm.getKeys(target))
					return false;
			return true;
		case ConditionInfo.OP_EQ: case ConditionInfo.OP_NE: case ConditionInfo.OP_GT: case ConditionInfo.OP_GE: case ConditionInfo.OP_LT: case ConditionInfo.OP_LE:
			if (this.key == null)
				return false;
			else {
				target.add(this.key);
				return true;
			}
		default:
			return false;
		}
	}

}
