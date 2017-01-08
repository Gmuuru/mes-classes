/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Seance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class DonneesHandler {
    
    private static final Logger LOG = LogManager.getLogger(DonneesHandler.class);
    
    private final ModelHandler model;
    
    private static DonneesHandler handler;
    
    public static DonneesHandler getInstance(){
        if(handler == null){
            init();
        }
        return handler;
    }
    
    public static void init(){
            handler = new DonneesHandler();
    }
    
    private DonneesHandler(){
        model = ModelHandler.getInstance();
    }
    
    public EleveData getDonneeForEleve(Seance seance, Eleve eleve){
        if(seance == null){
            return null;
        }
        if(seance.getDonnees().containsKey(eleve)){
            return seance.getDonnees().get(eleve);
        }
        return buildEleveData(seance, eleve);
    }
    
    public EleveData getOrCreateDonneeForEleve(Seance seance, Eleve eleve){
        if(seance == null){
            return null;
        }
        if(seance.getDonnees().containsKey(eleve)){
            return seance.getDonnees().get(eleve);
        }
        LOG.debug("No data for eleve {} at seance {}", eleve, seance.getId());
        EleveData data = buildEleveData(seance, eleve);
        persistEleveData(data);
        return data;
    }
    
    public EleveData buildEleveData(Seance seance, Eleve eleve){
        EleveData newData = new EleveData();
        newData.setEleve(eleve);
        newData.setSeance(seance);
        newData.setDate(seance.getDateAsDate());
        return newData;
    }
    
    public void persistEleveData(EleveData data){
        data.getSeance().getDonnees().put(data.getEleve(), data);
        data.getEleve().getData().add(data);
    }
}
