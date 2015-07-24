/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package eu.unifiedviews.helpers.dataunit.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            return df.format(date);
        } else {
            return null;
        }
    }

    public static Date stringToDate(String strDate) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date result = null;
        if (!isBlank(strDate)) {
            result = df.parse(strDate);
        }
        return result;
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(cs.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
