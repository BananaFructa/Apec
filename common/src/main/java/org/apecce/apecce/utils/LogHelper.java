package org.apecce.apecce.utils;

import org.apache.commons.lang3.StringUtils;
import org.apecce.apecce.ApecCE;

public class LogHelper {

    public static void section(String section) {
        ApecCE.getInstance().getLogger().info(StringUtils.center(" " + section + " ", 70, "="));
    }
}
