/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.util;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mesclasses.model.Classe;
import mesclasses.model.Eleve;
import mesclasses.model.Trimestre;
import mesclasses.util.validation.FError;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author rrrt3491
 */
public class Validators {
    
    public static List<FError> validate(Trimestre t){
        List<FError> e = Lists.newArrayList();
        if(t == null){
            e.add(new FError("trimestre null"));
            return e;
        }
        if(StringUtils.isBlank(t.getName())){
            e.add(new FError("trimestre : nom obligatoire", t));
        }
        if(t.getStartAsDate() == null){
            e.add(new FError("trimestre "+t+": début obligatoire", t));
        } else if(t.getEndAsDate() == null){
            e.add(new FError("trimestre "+t+": fin obligatoire", t));
        } else if(!t.getStartAsDate().isEqual(t.getEndAsDate()) 
                && !t.getStartAsDate().isBefore(t.getEndAsDate())){
            e.add(new FError("trimestre "+t+": dates incorrectes", t));
        }
        return e;
    }
    
    public static List<FError> validateTrimestreList(List<Trimestre> list){
        List<FError> err = Lists.newArrayList();
        Set<String> dupes = findDuplicates(list.stream().map( t -> t.getName() ).collect(Collectors.toList()));
        dupes.forEach(s -> {
            err.add(new FError("trimestre "+s+" en double"));
        });
        return err;
    }
    
    public static List<FError> validate(Classe c){
        List<FError> err = Lists.newArrayList();
        if(StringUtils.isBlank(c.getName())){
            err.add(new FError("classe : nom obligatoire", c));
        }
        if(c.getEleves() == null){
            err.add(new FError("classe "+c.getName()+": élèves null", c));
        } 
        return err;
    }
    
    public static List<FError> validateEleveList(List<Eleve> list){
        List<FError> err = Lists.newArrayList();
        Set<String> dupes = findDuplicates(list.stream().map( e -> e.getFullName() ).collect(Collectors.toList()));
        dupes.forEach(s -> {
            err.add(new FError("élève "+s+" en double"));
        });
        return err;
    }
    
    
    private static <T> Set<T> findDuplicates(Collection<T> list) {

    Set<T> duplicates = new HashSet<>();
    Set<T> uniques = new HashSet<>();

    list.stream().filter((t) -> (!uniques.add(t))).forEach((t) -> {
        duplicates.add(t);
    });

    return duplicates;
}
}
