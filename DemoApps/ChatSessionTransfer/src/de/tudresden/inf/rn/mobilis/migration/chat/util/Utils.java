/**
 * Copyright (C) 2009 Technische Universität Dresden
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

package de.tudresden.inf.rn.mobilis.migration.chat.util;

/**
 * A collection of utility methods for String objects.
 * 
 * @author koren
 */
public class Utils {

	public static String parseBareXMPPAddress(String XMPPAddress) {
		if (XMPPAddress == null) {
			return null;
		}
		int slashIndex = XMPPAddress.indexOf("/");
		if (slashIndex < 0) {
			return XMPPAddress;
		} else if (slashIndex == 0) {
			return "";
		} else {
			return XMPPAddress.substring(0, slashIndex);
		}
	}
	
	public static String parseResource(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex + 1 > XMPPAddress.length() || slashIndex < 0) {
            return "";
        }
        else {
            return XMPPAddress.substring(slashIndex + 1);
        }
    }

}
