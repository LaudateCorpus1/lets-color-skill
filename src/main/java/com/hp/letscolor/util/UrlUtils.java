/*
 * Copyright 2018 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.util;

import java.util.List;
import java.util.Random;

public class UrlUtils {

    public static String pickUrl(List<String> urls) {
        Random random = new Random();
        return urls.get(random.nextInt(urls.size()));
    }

    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
    }

    public static String getExtensionFromUrl(String url) {
        return url.substring(url.lastIndexOf(".") + 1).toUpperCase();
    }
}
