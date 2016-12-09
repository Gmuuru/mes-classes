/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.builders;

import java.time.LocalDate;
import mesclasses.model.Trimestre;

/**
 *
 * @author rrrt3491
 */
public class TrimestreBuilder {
    
    private Trimestre trimestre = new Trimestre();
    
    public TrimestreBuilder name(String name) {
        trimestre.setName(name);
        return this;
    }

    public TrimestreBuilder start(String start) {
        trimestre.setStart(start);
        return this;
    }

    public TrimestreBuilder start(LocalDate startDate) {
        trimestre.setStart(startDate);
        return this;
    }

    public TrimestreBuilder end(String end) {
        trimestre.setEnd(end);
        return this;
    }

    public TrimestreBuilder end(LocalDate endDate) {
        trimestre.setEnd(endDate);
        return this;
    }
    
    public Trimestre build(){
        return trimestre;
    }
    
}
