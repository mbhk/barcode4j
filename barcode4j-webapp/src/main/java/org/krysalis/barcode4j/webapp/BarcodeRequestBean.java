/*
 * Copyright 2002-2004,2007 Jeremias Maerki or contributors to Barcode4J, as applicable
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
package org.krysalis.barcode4j.webapp;

import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.krysalis.barcode4j.servlet.BarcodeServlet;
import org.krysalis.barcode4j.tools.MimeTypes;

/**
 * This is just a little helper bean for the JSP page.
 *
 * @version $Id$
 */
public class BarcodeRequestBean {

    private String type;
    private String msg;
    private String height;
    private String moduleWidth;
    private String wideFactor;
    private String quietZone;
    private String humanReadable;
    private String humanReadableSize;
    private String humanReadableFont;
    private String humanReadablePattern;
    private String format;
    private boolean svgEmbed;
    private String resolution;
    private boolean gray;

    public String getType() {
        return type;
    }

    public void setType(String string) {
        type = string;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String string) {
        height = string;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String string) {
        msg = string;
    }

    public String getModuleWidth() {
        return moduleWidth;
    }

    public void setModuleWidth(String string) {
        moduleWidth = string;
    }

    public String getWideFactor() {
        return wideFactor;
    }

    public void setWideFactor(String string) {
        wideFactor = string;
    }

    public String getQuietZone() {
        return quietZone;
    }

    public void setQuietZone(String string) {
        quietZone = string;
    }

    public String getHumanReadable() {
        if ("[default]".equals(humanReadable)) {
            return null;
        } else {
            return humanReadable;
        }
    }

    public void setHumanReadable(String string) {
        humanReadable = string;
    }

    public String getHumanReadableSize() {
        return humanReadableSize;
    }

    public void setHumanReadableSize(String humanReadableSize) {
        this.humanReadableSize = humanReadableSize;
    }

    public String getHumanReadableFont() {
        return humanReadableFont;
    }

    public void setHumanReadableFont(String humanReadableFont) {
        this.humanReadableFont = humanReadableFont;
    }

    public String getHumanReadablePattern(){
        return this.humanReadablePattern;
    }

    public void sethumanReadablePattern(String pattern){
        this.humanReadablePattern = pattern;
    }

    public String getFormat() {
        return this.format;
    }

    public boolean isSVG() {
        return MimeTypes.MIME_SVG.equals(MimeTypes.expandFormat(getFormat()))
            || (getFormat() == null)
            || (getFormat().length() == 0);
    }

    public boolean isSvgEmbed() {
        return this.svgEmbed;
    }

    public void setSvgEmbed(boolean value) {
        this.svgEmbed = value;
    }

    public boolean isBitmap() {
        return MimeTypes.isBitmapFormat(getFormat());
    }

    public void setFormat(String string) {
        this.format = string;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String string) {
        this.resolution = string;
    }

    public boolean isGray() {
        return this.gray;
    }

    public void setGray(boolean value) {
        this.gray = value;
    }

    public String toURL() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append("genbc?");

        //Type
        String reqType = getType();
        if (reqType == null) {
            reqType = "code128";
        }
        sb.append(BarcodeServlet.BARCODE_TYPE);
        sb.append("=");
        sb.append(reqType);

        //Message
        String reqMsg = getMsg();
        if (reqMsg == null) {
            reqMsg = "123456";
        }
        sb.append("&");
        sb.append(BarcodeServlet.BARCODE_MSG);
        sb.append("=");
        sb.append(encode(reqMsg));

        //Height
        final String reqHeight = getHeight();
        if (reqHeight != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_HEIGHT);
            sb.append("=");
            sb.append(reqHeight);
        }

        //Module Width
        final String reqModuleWidth = getModuleWidth();
        if (reqModuleWidth != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_MODULE_WIDTH);
            sb.append("=");
            sb.append(reqModuleWidth);
        }

        //Wide Factor
        final String reqWideFactor = getWideFactor();
        if (reqWideFactor != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_WIDE_FACTOR);
            sb.append("=");
            sb.append(reqWideFactor);
        }

        //Quiet Zone
        final String reqQuietZone = getQuietZone();
        if (reqQuietZone != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_QUIET_ZONE);
            sb.append("=");
            sb.append(reqQuietZone);
        }

        //Human Readable Part
        final String reqHumanReadable = getHumanReadable();
        if (reqHumanReadable != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_HUMAN_READABLE_POS);
            sb.append("=");
            sb.append(reqHumanReadable);
        }

        //Output Format
        final String fmt = getFormat();
        if (fmt != null && !isSVG()) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_FORMAT);
            sb.append("=");
            sb.append(fmt);
        }

        final String reqHumanReadableSize = getHumanReadableSize();
        if (reqHumanReadableSize != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_HUMAN_READABLE_SIZE);
            sb.append("=");
            sb.append(reqHumanReadableSize);
        }

        final String reqHumanReadableFont = getHumanReadableFont();
        if (reqHumanReadableFont != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_HUMAN_READABLE_FONT);
            sb.append("=");
            sb.append(encode(reqHumanReadableFont));
        }

        final String hrPattern = getHumanReadablePattern();
        if (hrPattern != null) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_HUMAN_READABLE_PATTERN);
            sb.append("=");
            sb.append(hrPattern);
        }

        //Output Format
        final String res = getResolution();
        if (res != null && isBitmap()) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_IMAGE_RESOLUTION);
            sb.append("=");
            sb.append(res);
        }

        //Output Format
        final boolean reqGray = isGray();
        if (reqGray && isBitmap()) {
            sb.append("&");
            sb.append(BarcodeServlet.BARCODE_IMAGE_GRAYSCALE);
            sb.append("=");
            sb.append(isGray() ? "true" : "false");
        }

        return sb.toString();
    }

    public String toXMLSafeURL() {
        return escapeForXML(toURL());
    }

    private static String encode(String text) {
        try {
            return java.net.URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Incompatible JVM: " + e.getMessage(), e);
        }
    }

    public static String escapeForXML(String text) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                // the char is not a special one
                // add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
}
