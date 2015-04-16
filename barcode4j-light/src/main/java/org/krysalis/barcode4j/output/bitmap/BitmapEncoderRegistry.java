/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.output.bitmap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

/**
 * Registry class for BitmapEncoders.
 *
 * @author Jeremias Maerki
 * @version 1.2
 */
public final class BitmapEncoderRegistry {

    private static final Set<Entry> encoders = new java.util.TreeSet<Entry>(new Comparator<Entry>() {

        @Override
        public int compare(Entry o1, Entry o2) {
            //highest priority first
            return o1.priority - o2.priority;
        }
    });

    static {
        register(org.krysalis.barcode4j.output.bitmap.ImageIOBitmapEncoder.class.getName(),
                0, false);
    }

    /**
     * Utility class: No instantiation.
     */
    private BitmapEncoderRegistry() {
        // intentionally left blank
    }

    private static class Entry {

        private final BitmapEncoder encoder;
        private final int priority;

        public Entry(BitmapEncoder encoder, int priority) {
            this.encoder = encoder;
            this.priority = priority;
        }
    }

    /**
     * Register a new BitmapEncoder implementation.
     *
     * @param classname fully qualified classname of the BitmapEncoder
     * implementation
     * @param priority lets you define a priority for an encoder. If you want to
     * give an encoder a high priority, assign a value of 100 or higher.
     * @param complain throw an exception in case of failure
     */
    private static synchronized void register(String classname, int priority, boolean complain) {
        Throwable error = null;
        try {
            final Class<?> clazz = Class.forName(classname);
            final BitmapEncoder encoder = (BitmapEncoder) clazz.newInstance();
            encoders.add(new Entry(encoder, priority));
        } catch (ClassNotFoundException e) {
            error = e;
        } catch (InstantiationException e) {
            error = e;
        } catch (IllegalAccessException e) {
            error = e;
        }
        if (error != null && complain) {
            throw new IllegalArgumentException(
                    "The implementation being registered is unavailable or "
                    + "cannot be instantiated: " + classname, error);
        }
    }

    /**
     * Register a new BitmapEncoder implementation.
     *
     * @param classname fully qualified classname of the BitmapEncoder
     * implementation
     * @param priority lets you define a priority for an encoder. If you want to
     * give an encoder a high priority, assign a value of 100 or higher.
     */
    public static void register(String classname, int priority) {
        register(classname, priority, true);
    }

    /**
     * Indicates whether a specific BitmapEncoder implementation supports a
     * particular MIME type.
     *
     * @param encoder BitmapEncoder to inspect
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(BitmapEncoder encoder, String mime) {
        for (final String mime1 : encoder.getSupportedMIMETypes()) {
            if (mime1.equals(mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether a particular MIME type is supported by one of the
     * registered BitmapEncoder implementations.
     *
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(String mime) {
        for (final Entry entry : encoders) {
            final BitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a BitmapEncoder instance for a particular MIME type.
     *
     * @param mime desired MIME type
     * @return a BitmapEncoder instance (throws an UnsupportedOperationException
     * if no suitable BitmapEncoder is available)
     */
    public static BitmapEncoder getInstance(String mime) {
        for (final Entry entry : encoders) {
            final BitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return encoder;
            }
        }
        throw new UnsupportedOperationException(
                "No BitmapEncoder available for " + mime);
    }

    /**
     * Returns a Set of Strings with all the supported MIME types from all
     * registered BitmapEncoders.
     *
     * @return a Set of Strings (MIME types)
     */
    public static Set<String> getSupportedMIMETypes() {
        final Set<String> mimes = new java.util.HashSet<String>();
        for (final Entry entry : encoders) {
            mimes.addAll(Arrays.asList(entry.encoder.getSupportedMIMETypes()));
        }
        return mimes;
    }
}
