package org.apec.apec.managers;

import org.apec.apec.Apec;
import org.apec.apec.module.Module;
import org.apec.apec.module.modules.ApecMenu;
import org.reflections.Reflections;

import java.util.ArrayList;

public class ModuleManager {

    private final ArrayList<Module> modules = new ArrayList<>();

    public void initialize() {
        registerMods();
        Apec.getInstance().getLogger().info("Modules registered: " + modules.size());
        modules.forEach(module -> Apec.getInstance().getLogger().info("Module registered: " + module.getName()));
    }

    public void postInitialize() {
        modules.forEach(Module::postInit);
    }

    private void registerMods() {
        if (Apec.getInstance().getModLoader() == Apec.ModLoader.FORGE) {
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

    public <T extends Module> Module getModuleByClass(final Class<T> clazz) {
        return modules.stream().filter(module -> module.getClass().equals(clazz)).findFirst().map(clazz::cast).orElse(null);
    }

}
