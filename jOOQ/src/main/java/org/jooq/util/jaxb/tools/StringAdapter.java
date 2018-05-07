/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.util.jaxb.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jooq.tools.StringUtils;

/**
 * An {@link XmlAdapter} that implements useful features after parsing XML
 * strings with JAXB.
 * <p>
 * Supported features are:
 * <ul>
 * <li>[#2401] String-trimming, taking out whitespace from JAXB-bound XML
 * content.</li>
 * <li>[#4550] Property expression resolution</li>
 * </ul>
 *
 * @author Lukas Eder
 */
public class StringAdapter extends XmlAdapter<String, String> {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");
    
    private boolean isNullString(String v) {
    	return v == null;
    }
    private String result, group0, group1;
    private void initResult(String vTrim) {result = vTrim;}
    private String getResult() {return result;}
    private void initGroup0(String matcherGroup0) {group0 = matcherGroup0;}
    private void initGroup1(String matcherGroup1) {group1 = matcherGroup1;}
    private String getGroup0() {return group0;}
    private String getGroup1() {return group1;}
    
    @Override
    public final String unmarshal(String v) throws Exception {
        if (isNullString(v))
            return null;

        initResult(v.trim());

        Matcher matcher = PROPERTY_PATTERN.matcher(getResult());
        while (matcher.find()) {
            initGroup0(matcher.group(0));
            initGroup1(matcher.group(1));

            initResult(StringUtils.replace(getResult(), getGroup0(), System.getProperty(getGroup1(), getGroup0())));
        }

        return getResult();
    }

    @Override
    public final String marshal(String v) throws Exception {
        if (isNullString(v))
            return null;
        initResult(v.trim());
        return getResult();
    }
}
