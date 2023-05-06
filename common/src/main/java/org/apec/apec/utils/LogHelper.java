package org.apec.apec.utils;

import org.apache.commons.lang3.StringUtils;
import org.apec.apec.Apec;

public class LogHelper {

    public static void section(String section) {
        Apec.getInstance().getLogger().info(StringUtils.center(" " + section + " ", 70, "="));
    }
}
