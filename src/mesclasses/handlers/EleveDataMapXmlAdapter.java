/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Gilles
 */
public class EleveDataMapXmlAdapter extends XmlAdapter<EleveDataMapXmlAdapter.AdaptedProperties, Map<Eleve, EleveData>>{

    private static final Logger LOG = LogManager.getLogger(EleveDataMapXmlAdapter.class);
    
    public static class AdaptedProperties {
        public List<Property> property = new ArrayList<>();
    }

    public static class Property {
        @XmlAttribute
        @XmlIDREF
        public Eleve eleve;

        @XmlValue
        @XmlIDREF
        public EleveData data;
    }

    @Override
    public ObservableMap<Eleve, EleveData> unmarshal(AdaptedProperties adaptedProperties) throws Exception {
        if(null == adaptedProperties) {
            return null;
        }
        ObservableMap<Eleve, EleveData> map = FXCollections.observableHashMap();
        adaptedProperties.property.forEach((property) -> {
            map.put(property.eleve, property.data);
        });
        //LOG.debug("Unarshalling map of {} properties to {} élèves", adaptedProperties.property.size(), map.size());
        return map;
    }

    @Override
    public AdaptedProperties marshal(Map<Eleve, EleveData> map) throws Exception {
        if(null == map) {
            return null;
        }
        AdaptedProperties adaptedProperties = new AdaptedProperties();
        map.entrySet().stream().map((entry) -> {
            Property property = new Property();
            property.eleve = entry.getKey();
            property.data = entry.getValue();
            return property;
        }).forEach((property) -> {
            adaptedProperties.property.add(property);
        });
        //LOG.debug("Marshalling map of {} élèves to {} properties", map.size(), adaptedProperties.property.size());
        return adaptedProperties;
    }

}