/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.controller;

import java.time.LocalDate;
import mesclasses.model.Classe;

/**
 *
 * @author rrrt3491
 */
public abstract class TabContentController extends PageController {
    
    public abstract void setClasse(Classe classe);
    
    public void setCurrentDate(LocalDate date){};
}
