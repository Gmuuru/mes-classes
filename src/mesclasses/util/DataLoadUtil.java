/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mesclasses.model.Constants;
import mesclasses.model.datamodel.ObservableData;
import mesclasses.model.datamodel.XMLData;

/**
 *
 * @author rrrt3491
 */
public class DataLoadUtil {
    
    public static ObservableData initializeData(File saveFile) throws Exception {
        
        ObservableData data = new ObservableData();
        XMLData xmlData = initXMLData(saveFile);
        data.getTrimestres().addAll(xmlData.getTrimestres());
        data.getClasses().addAll(xmlData.getClasses());
        data.getCours().addAll(xmlData.getCours());
        xmlData.getJournees().keySet().forEach(dateStr -> {
            data.getJournees().put(LocalDate.parse(dateStr, Constants.DATE_FORMATTER), xmlData.getJournees().get(dateStr));
        });
        return data;
    }
    
    private static XMLData initXMLData(File saveFile) throws Exception{
        
        if(!FileSaveUtil.saveFileExists()){
            FileSaveUtil.createNewSaveFile();
            return new XMLData();
        }
        XMLData xmlData = readData(saveFile);
        
        if(xmlData == null){
            xmlData = new XMLData();
        }
        return xmlData;
    }
    
    public static boolean isfileEmpty(File file){
        BufferedReader br = null;     
        try {
            br = new BufferedReader(new FileReader(file));
            if (br.readLine() == null) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(DataLoadUtil.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(DataLoadUtil.class.getName()).log(Level.SEVERE, null, ex);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void writeData(XMLData data, File file){
        
        Marshaller m;
        try {
            JAXBContext context = JAXBContext.newInstance(XMLData.class);
            m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            AppLogger.log(e);
            return;
        }
        try {
            // Marshalling and saving XML to the file.
            m.marshal(data, file);
        
        } catch (Exception e) { // catches ANY exception
            AppLogger.log(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

   
    public static XMLData readData(File file) throws Exception {
        
        if(isfileEmpty(file)){
            return new XMLData();
        }
        
        JAXBContext context = JAXBContext
                .newInstance(XMLData.class);
        Unmarshaller um = context.createUnmarshaller();
        // Reading XML from the file and unmarshalling.
        XMLData data = (XMLData)um.unmarshal(file);
        if(data.getTrimestres() == null){
            AppLogger.log("erreur fichier xml !");
        }
        return data;
    }
    

}
