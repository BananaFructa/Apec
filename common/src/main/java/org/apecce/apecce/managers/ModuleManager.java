package org.apecce.apecce.managers;

import org.apecce.apecce.ApecCE;
import org.apecce.apecce.module.Module;
import org.apecce.apecce.module.modules.ApecMenu;
import org.reflections.Reflections;

import java.util.ArrayList;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<>();

    public void initialize() {
        registerMods();
        ApecCE.getInstance().getLogger().info("Modules registered: " + modules.size());
        modules.forEach(module -> ApecCE.getInstance().getLogger().info("Module registered: " + module.getName()));
    }

    private void registerMods() {
        if (ApecCE.getInstance().getModLoader() == ApecCE.ModLoader.FORGE) {
            // Fuck forge not dynamically finding them
            modules.add(new ApecMenu());
            return;
        }

        String packageName = Module.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Module.ModuleInfo.class)) {
            try {
                Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                modules.add(module);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

}
