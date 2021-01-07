/**
 * Saves data on disk for all the components
 */
package Apec.DataInterpretation;

import Apec.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ComponentSaveManager {

    public List<Component> ComponentList;

    public ComponentSaveManager(List<Component> components) {
        this.ComponentList = components;
    }

    public void IssueSave() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/ComponentData.txt").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/ComponentData.txt");

            String DataBuff = "";

            for (Component component : ComponentList) {
                if (component.hasDataToSave()) {
                    for (Integer DataID : component.DataToSave.keySet()) {
                        DataBuff += component.componentId.ordinal() + "!" + DataID + "#" + component.DataToSave.get(DataID) + "\n";
                    }
                }
            }
            if (DataBuff.length() != 0)
            DataBuff = DataBuff.substring(0,DataBuff.length()-1);

            fw.write(DataBuff);
            fw.close();

        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public List<HashMap<Integer,String>> LoadData() {
        List<HashMap<Integer,String>> Data = new ArrayList<HashMap<Integer, String>>();
        for (int i = 0;i < ComponentList.size();i++) Data.add(new HashMap<Integer, String>());

        try {

            Scanner scanner = new Scanner(new File("config/Apec/ComponentData.txt"));
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();

                String[] tempSplit = s.split("#");
                String[] _tempSplit = tempSplit[0].split("!");
                int id = Integer.parseInt(_tempSplit[0]);
                int code = Integer.parseInt(_tempSplit[1]);
                String data = tempSplit[1];

                Data.get(id).put(code,data);

            }


        } catch (IOException err) {
            err.printStackTrace();
        }

        return Data;

    }

}
