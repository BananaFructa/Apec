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

    public void postInitialize() {
        modules.forEach(Module::postInit);
    }

    private void registerMods() {
        if (ApecCE.getInstance().getModLoader() == ApecCE.ModLoader.FORGE) {
            // Fuck forge not dynamically finding them
            modules.add(new ApecMenu());
            modules.get(0).toggle();
            return;
        }

        String packageName = Module.class.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Module.ModuleInfo.class)) {
            try {
                Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                modules.add(module);
                if (clazz.isAnnotationPresent(Module.ModuleEnabled.class)) {
                    module.toggle();
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

}
