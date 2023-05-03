package org.apecce.apecce.module.modules;

import org.apecce.apecce.module.Module;

@Module.ModuleInfo(name = "ApecMenu", description = "ApecCE's menu")
public class ApecMenu extends Module {

    @Override
    protected void onToggle() {
        System.out.println("ApecMenu toggled");
    }
}
