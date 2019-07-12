/*
 * Copyright 2019 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

public class I18nResource {

    private static final String BASE_NAME = "locale/messages";

    private final static WeakHashMap<Locale, ResourceBundle> cache = new WeakHashMap<>();

    public static String getString(String key, Locale locale) {
        ResourceBundle resources = cache.get(locale);

        if (resources == null) {
            resources = ResourceBundle.getBundle(BASE_NAME, locale);
            cache.put(locale, resources);
        }
        try {
            return resources.getString(key);
        } catch (MissingResourceException mre) {
            return key;
        }
    }
}
