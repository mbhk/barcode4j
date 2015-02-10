/*
 * Copyright 2008 Jeremias Maerki
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

package org.krysalis.barcode4j.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for pre-processing messages.
 * 
 * @version 1.2
 */
public class MessageUtil {

    private static final Pattern UNESCAPE_UNICODE_PATTERN = Pattern.compile("(\\\\\\\\)|(\\\\u([\\dA-Fa-f]{0,4}))");

    private MessageUtil() {
    }

    /**
     * Un-escapes escaped Unicode characters in a message.
     *
     * This is used to support characters not encodable in XML, such as the RS
     * or GS characters.
     *
     * @param message the message
     * @return the processed message
     */
    public static String unescapeUnicode(final String message) {
        if (message == null) {
            return null;
        }

        final StringBuilder res = new StringBuilder();
        int processedUntil = 0;

        final Matcher m = UNESCAPE_UNICODE_PATTERN.matcher(message);
        while (m.find()) {
            res.append(message.substring(processedUntil, m.start()));
            if (m.group(1) != null) {
                res.append('\\');
            } else {
                final String substr = message.substring(m.start(3), m.end(3));
                if (m.end(3) - m.start(3) != 4) {
                    throw new IllegalArgumentException("Unfinished Unicode-Sequence in `" + message + "` (" + substr + ")");
                }
                final char c = (char) Integer.parseUnsignedInt(substr, 16);
                res.append(c);
            }
            processedUntil = m.end();
        }
        res.append(message.substring(processedUntil));

        return res.toString();
    }

    /**
     * Filters non-printable ASCII characters (0-31 and 127) from a string with
     * spaces and returns that. Please note that non-printable characters
     * outside the ASCII character set are not touched by this method.
     *
     * @param text the text to be filtered.
     * @return the filtered text
     */
    public static String filterNonPrintableCharacters(String text) {
        final int len = text.length();
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            final char ch = text.charAt(i);
            if (ch < 32 || ch == 127) {
                sb.append(' '); //Replace non-printables with a space
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
