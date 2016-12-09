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
import org.apache.logging.log4j.LogManager;
import org.zeroturnaround.zip.commons.FileUtils;

/**
 *
 * @author rrrt3491
 */
public class DataLoadUtil {
    
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(DataLoadUtil.class);
    
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
    
    public static boolean writeData(XMLData data, File file){
        
        Marshaller m;
        String error = null;
        File tmpFile = new File("tmpSave_"+LocalDate.now().format(Constants.DATE_FORMATTER));
        try {
            FileUtils.deleteQuietly(tmpFile);
            tmpFile.createNewFile();
            JAXBContext context = JAXBContext.newInstance(XMLData.class);
            m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException | IOException e) {
            LOG.error("writeData error :", e);
            return false;
        }
        try {
            // Marshalling and saving XML to the file.
            m.marshal(data, tmpFile);
            FileUtils.copyFile(tmpFile, file);
        
        } catch (JAXBException e) {
            error = "Erreur de formattage des données";
                return false;
        } catch (IOException e) {
            LOG.error("writeData error :", e);
            error = "Erreur d'accès au fichier "+file.getPath();
                return false;
        } catch (Exception e) {
            LOG.error("writeData error :", e);
            error = "Erreur interne de l'application";
                return false;
        } finally {
            if(error != null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Sauvegarde impossible");
                alert.setContentText(error);
                alert.showAndWait();
            } else {
                FileUtils.deleteQuietly(tmpFile);
            }
        }
        return true;
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
            LOG.error("erreur fichier xml !");
        }
        return data;
    }
    

}
