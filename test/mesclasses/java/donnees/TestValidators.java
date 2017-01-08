/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.java.donnees;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mesclasses.java.builders.ChangementClasseBuilder;
import mesclasses.java.builders.ClasseBuilder;
import mesclasses.java.builders.CoursBuilder;
import mesclasses.java.builders.DevoirBuilder;
import mesclasses.java.builders.DonneeBuilder;
import mesclasses.java.builders.EleveBuilder;
import mesclasses.java.builders.JourneeBuilder;
import mesclasses.java.builders.MotBuilder;
import mesclasses.java.builders.PunitionBuilder;
import mesclasses.java.builders.SeanceBuilder;
import mesclasses.java.builders.TrimestreBuilder;
import mesclasses.java.util.MyAssert;
import mesclasses.model.ChangementClasse;
import mesclasses.model.Classe;
import mesclasses.model.Cours;
import mesclasses.model.Devoir;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.FileConfigurationManager;
import mesclasses.model.Journee;
import mesclasses.model.Mot;
import mesclasses.model.Punition;
import mesclasses.model.Seance;
import mesclasses.model.Trimestre;
import mesclasses.util.validation.FError;
import mesclasses.util.validation.ListValidators;
import mesclasses.util.validation.Validators;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rrrt3491
 */
public class TestValidators {
    
    private final LocalDate NULL_DATE = null;
    private final LocalDate TEST_DATE = LocalDate.of(2016, Month.DECEMBER, 15);
    private final LocalDate TEST_DATE2 = LocalDate.of(2016, Month.DECEMBER, 16);
    private final LocalDate TEST_DATE3 = LocalDate.of(2016, Month.DECEMBER, 17);
    
    static {
        try {
            FileConfigurationManager.autoDetect();
        } catch (FileNotFoundException ex) {
            System.out.println("Impossible de charger la configuration...");
        } 
        FileConfigurationManager.setLog4JConf();
    }
    
    @Test
    public void trimestreValidatorsTest(){
        
        Trimestre t = new TrimestreBuilder()
                .name("junit")
                .start("2016-09-01")
                .end("2016-12-31").build();
        
        //OK
        List<FError> err = t.validate();
        MyAssert.assertEmpty(err);
        
        // name null
        t.setName(null);
        t.setStart(NULL_DATE);
        t.setEnd(NULL_DATE);
        err = t.validate();
        MyAssert.assertSize(err, 3);
        
        MyAssert.assertContainsError(err, "Trimestre null : nom obligatoire");
        MyAssert.assertContainsError(err, "Trimestre null : début obligatoire");
        MyAssert.assertContainsError(err, "Trimestre null : fin obligatoire");
        
        // start = end 
        t.setName("junit");
        t.setStart("2016-09-01");
        t.setEnd("2016-09-01");
        err = t.validate();
        MyAssert.assertEmpty(err);
        
        // start > end 
        t.setStart("2016-09-02");
        t.setEnd("2016-09-01");
        err = t.validate();
        MyAssert.assertContainsSingleError(err, "Trimestre junit : dates invalides");
        
    }
    
    @Test
    public void trimestreListValidatorsTest(){
        List<Trimestre> list = Lists.newArrayList(
            new TrimestreBuilder().name(null).start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name(null).start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name("junit").start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name("junit").start("2016-09-01").end("2016-12-31").build()  ,
            new TrimestreBuilder().name("junit2").start("2016-09-01").end("2016-12-31").build() , 
            new TrimestreBuilder().name("junit2").start("2016-09-01").end("2016-12-31").build() ,
            new TrimestreBuilder().name("junit3").start("2016-09-01").end("2016-08-31").build()  
        );
        List<FError> err = ListValidators.validateTrimestreList(list);
        MyAssert.assertSize(err, 6);
        MyAssert.assertContainsError(err, "Trimestre null : nom obligatoire");
        MyAssert.assertContainsError(err, "Trimestre null en double");
        MyAssert.assertContainsError(err, "Trimestre junit en double");
        MyAssert.assertContainsError(err, "Trimestre junit2 en double");
        MyAssert.assertContainsError(err, "Trimestre junit3 : dates invalides");
        
        err = ListValidators.validateTrimestreList(null);
        MyAssert.assertContainsSingleError(err, "Liste trimestres null");
        
    }
    
    @Test
    public void classeValidatorsTest(){
        ClasseBuilder cb = new ClasseBuilder();
        Classe c = cb.name("junit").build();
        
        //OK
        List<FError> err = c.validate();
        MyAssert.assertEmpty(err);
        
        // name null
        c = cb.name("junit").eleves(null).build();
        err = c.validate();
        MyAssert.assertContainsSingleError(err, "Classe junit : liste élèves null");
        
        c = cb.eleve(null).build();
        err = c.validate();
        MyAssert.assertContainsSingleError(err, "Classe junit : Elève null");
    }
    
    @Test
    public void classeListValidatorsTest(){
        List<Classe> list = Lists.newArrayList(
            new ClasseBuilder().name(null).build()  ,
            new ClasseBuilder().name(null).build()  ,
            new ClasseBuilder().name("junit").build()  ,
            new ClasseBuilder().name("junit").build()  ,
            new ClasseBuilder().name("junit2").build() , 
            new ClasseBuilder().name("junit2").build() ,
            new ClasseBuilder().name("junit3").eleve(null).build()  
        );
        List<FError> err = ListValidators.validateClasseList(list);
        MyAssert.assertSize(err, 6);
        MyAssert.assertContainsError(err, "Classe null : nom obligatoire");
        MyAssert.assertContainsError(err, "Classe null en double");
        MyAssert.assertContainsError(err, "Classe junit en double");
        MyAssert.assertContainsError(err, "Classe junit2 en double");
        MyAssert.assertContainsError(err, "Classe junit3 : Elève null");
        
        err = ListValidators.validateClasseList(null);
        MyAssert.assertContainsSingleError(err, "Liste classes null");
    }
    
    @Test
    public void eleveValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        
        //OK
        Eleve e = eb.firstName("junit").lastName("junit").classe(new Classe()).build();
        List<FError> err = e.validate();
        MyAssert.assertEmpty(err);
        
        e = eb.firstName("test").lastName("junit").classe(null).build();
        err = e.validate();
        MyAssert.assertContainsSingleError(err, "Elève test junit : classe obligatoire");
        
        e = eb.firstName(null).lastName(null).classe(null).build();
        err = e.validate();
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : nom obligatoire");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : prénom obligatoire");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : classe obligatoire");
        
    }
    
    @Test
    public void eleveListValidatorsTest(){
        ClasseBuilder cb = new ClasseBuilder();
        Classe classe = cb.name("junit").build();
        classe = cb
        .eleve(new EleveBuilder().classe(classe).firstName(null).lastName(null).build())
        .eleve(new EleveBuilder().classe(classe).firstName(null).lastName("test").build())
        .eleve(new EleveBuilder().classe(classe).firstName("junit").lastName("test").build())
        .eleve(new EleveBuilder().classe(classe).firstName("junit").lastName("test").build())
        .eleve(new EleveBuilder().classe(classe).firstName("junit2").lastName("test").build())
        .eleve(new EleveBuilder().classe(classe).firstName("junit2").lastName("test2").build())
        .eleve(new EleveBuilder().classe(new Classe()).firstName("junit3").lastName("ok").build()).build();  
        
        List<FError> err = ListValidators.validateEleveList(classe);
        MyAssert.assertSize(err, 5);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : nom obligatoire");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : prénom obligatoire");
        MyAssert.assertContainsError(err, "Elève <vide> test : prénom obligatoire");
        MyAssert.assertContainsError(err, "Classe junit : Elève junit test en double");
        MyAssert.assertContainsError(err, "Classe junit : l'Elève junit3 ok n'a pas la bonne classe");
        
        classe = cb.eleves(null).build();
        err = ListValidators.validateEleveList(classe);
        MyAssert.assertContainsSingleError(err, "Classe junit : liste élèves null");
    }
    
    @Test
    public void coursValidatorsTest(){
        CoursBuilder cb = new CoursBuilder();
        Cours c = cb.id(null).build();
        List<FError> err = Validators.validate(c);
        MyAssert.assertContainsSingleError(err, "Cours null ou sans Id");
        
        c = cb.id("junit").build();
        err = Validators.validate(c);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Cours junit : classe obligatoire");
        MyAssert.assertContainsError(err, "Cours junit : jour obligatoire");
        MyAssert.assertContainsError(err, "Cours junit : périodicité obligatoire");
        
        c = cb.id("junit").classe().day("Lundi").ponctuel(true).start("9h30").end("10h").build();
        err = Validators.validate(c);
        MyAssert.assertEmpty(err);
    }
    
    @Test
    public void coursListValidatorsTest(){
        List<Cours> list = Lists.newArrayList(
            null,
            new CoursBuilder().id(null).build(),
            new CoursBuilder().id("junit").classe().day("Lundi").ponctuel(true).start("9h30").end("10h").build());  
        
        List<FError> err = ListValidators.validateCoursList(list);
        MyAssert.assertSize(err, 2);
        MyAssert.assertContainsError(err, "Cours null");
        MyAssert.assertContainsError(err, "Cours null ou sans Id");
    }
    
    @Test
    public void punitionValidatorsTest(){
        PunitionBuilder pb = new PunitionBuilder();
        Punition p = pb.id(null).build();
        List<FError> err  = Validators.validate(p);
        MyAssert.assertContainsSingleError(err, "Punition null ou sans Id");
        
        p = pb.id("junit").build();
        err  = Validators.validate(p);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Punition junit : texte obligatoire");
        MyAssert.assertContainsError(err, "Punition junit : élève obligatoire");
        MyAssert.assertContainsError(err, "Punition junit : séance obligatoire");
        
        p = pb.seance().texte("test").eleve().build();
        err  = Validators.validate(p);
        MyAssert.assertEmpty(err);
    }
    
    @Test
    public void punitionListValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        Eleve e = eb
            .punition(null)
            .punition(new PunitionBuilder().id(null).build())
            .punition(new PunitionBuilder().id("junit").texte("test").eleve().seance().build())
        .build();  
        
        List<FError> err = ListValidators.validatePunitionList(e);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : punition null");
        MyAssert.assertContainsError(err, "Punition null ou sans Id");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : la Punition junit n'a pas le bon élève");
    }
    
    @Test
    public void devoirValidatorsTest(){
        DevoirBuilder pb = new DevoirBuilder();
        Devoir p = pb.id(null).build();
        List<FError> err  = Validators.validate(p);
        MyAssert.assertContainsSingleError(err, "Devoir null ou sans Id");
        
        p = pb.id("junit").build();
        err  = Validators.validate(p);
        MyAssert.assertSize(err, 2);
        MyAssert.assertContainsError(err, "Devoir junit : élève obligatoire");
        MyAssert.assertContainsError(err, "Devoir junit : séance obligatoire");
        
        p = pb.seance().eleve().build();
        err  = Validators.validate(p);
        MyAssert.assertEmpty(err);
    }
    
    @Test
    public void DevoirListValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        Eleve e = eb
            .devoir(null)
            .devoir(new DevoirBuilder().id(null).build())
            .devoir(new DevoirBuilder().id("junit").eleve().seance().build())
        .build();  
        
        List<FError> err = ListValidators.validateDevoirList(e);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : devoir null");
        MyAssert.assertContainsError(err, "Devoir null ou sans Id");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : le Devoir junit n'a pas le bon élève");
    }
    
    @Test
    public void motValidatorsTest(){
        MotBuilder pb = new MotBuilder();
        Mot p = pb.id(null).build();
        List<FError> err  = Validators.validate(p);
        MyAssert.assertContainsSingleError(err, "Mot carnet null ou sans Id");
        
        p = pb.id("junit").build();
        err  = Validators.validate(p);
        MyAssert.assertSize(err, 2);
        MyAssert.assertContainsError(err, "Mot carnet junit : élève obligatoire");
        MyAssert.assertContainsError(err, "Mot carnet junit : séance obligatoire");
        
        p = pb.seance().eleve().build();
        err  = Validators.validate(p);
        MyAssert.assertEmpty(err);
    }
    
    @Test
    public void MotListValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        Eleve e = eb
            .mot(null)
            .mot(new MotBuilder().id(null).build())
            .mot(new MotBuilder().id("junit").eleve().seance().build())
        .build();  
        
        List<FError> err = ListValidators.validateMotList(e);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : mot carnet null");
        MyAssert.assertContainsError(err, "Mot carnet null ou sans Id");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : le Mot carnet junit n'a pas le bon élève");
    }
    
    @Test
    public void donneeValidatorTest(){
        DonneeBuilder db = new DonneeBuilder();
        EleveData d = db.id(null).build();
        List<FError> err  = Validators.validate(d);
        MyAssert.assertContainsSingleError(err, "Donnée null ou sans Id");
        
        d = db.id("junit").eleve().build();
        err  = Validators.validate(d);
        MyAssert.assertContainsSingleError(err, "Donnée Elève <vide> <vide> (junit) : séance obligatoire");
        
        d = db.id("junit").eleve(null).seance().build();
        err  = Validators.validate(d);
        MyAssert.assertContainsSingleError(err, "Donnée (junit) : élève obligatoire");
        
        Assert.assertTrue(d.isEmpty());
        Assert.assertTrue(d.isEmpty());
    }
    
    @Test
    public void donneeListValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        Eleve e = eb
            .donnee(null)
            .donnee(new DonneeBuilder().id(null).build())
            .donnee(new DonneeBuilder().id("junit").eleve().seance().build())
        .build();  
        
        List<FError> err = ListValidators.validateDonneeList(e);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : Donnée null");
        MyAssert.assertContainsError(err, "Donnée null ou sans Id");
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : la Donnée Elève <vide> <vide> (junit) n'a pas le bon élève");
    }
    
    @Test
    public void changementValidatorsTest(){
        ChangementClasseBuilder cb = new ChangementClasseBuilder();
        ChangementClasse c = cb.date(NULL_DATE).build();
        List<FError> err  = Validators.validate(c);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Changement Classe null, null : classe obligatoire");
        MyAssert.assertContainsError(err, "Changement Classe null, null : type obligatoire");
        MyAssert.assertContainsError(err, "Changement Classe null, null : date obligatoire");
        
        c = cb.classe().date(TEST_DATE).type("test").build();
        err  = Validators.validate(c);
        MyAssert.assertEmpty(err);
    }
    
    @Test
    public void changementListValidatorsTest(){
        EleveBuilder eb = new EleveBuilder();
        Eleve e = eb
            .changement(null)
            .changement(new ChangementClasseBuilder().classe(new ClasseBuilder().name("junitClasse").build()).date(NULL_DATE).type("test").build())
            .changement(new ChangementClasseBuilder().classe().date(TEST_DATE).type("test").build())
        .build();  
        
        List<FError> err = ListValidators.validateChangementList(e);
        MyAssert.assertSize(err, 2);
        MyAssert.assertContainsError(err, "Elève <vide> <vide> : Changement null");
        MyAssert.assertContainsError(err, "Changement Classe junitClasse, test : date obligatoire");
    }
    
    @Test
    public void journeeValidatorsTest(){
        JourneeBuilder jb = new JourneeBuilder();
        Journee j = jb.date(NULL_DATE).build();
        List<FError> err  = Validators.validate(j);
        MyAssert.assertContainsSingleError(err, "Journée : date obligatoire");
        
        j = jb.date(TEST_DATE).build();
        MyAssert.assertEmpty(Validators.validate(j));
        MyAssert.assertContainsSingleError(err, "Journée : date obligatoire");
    }
    
    @Test
    public void journeeMapValidatorsTest(){
        Map<LocalDate, Journee> map = new HashMap<>();
        map.put(TEST_DATE, new JourneeBuilder().date(TEST_DATE).seances(null).build());
        map.put(null, new JourneeBuilder().date(TEST_DATE).build());
        map.put(TEST_DATE2, new JourneeBuilder().date(TEST_DATE3).build());
        map.put(TEST_DATE3, new JourneeBuilder().build());
        List<FError> err  = ListValidators.validateJourneeMap(map);
        MyAssert.assertSize(err, 4);
        MyAssert.assertContainsError(err, "Erreur : date vide dans le calendrier des journées");
        MyAssert.assertContainsError(err, "Journée 2016-12-15 : Liste séances null");
        MyAssert.assertContainsError(err, "Journée 2016-12-17 : mauvaise date");
        MyAssert.assertContainsError(err, "Journée : date obligatoire");
    }
    
    @Test
    public void seanceValidatorsTest(){
        SeanceBuilder sb = new SeanceBuilder();
        Seance s = sb.id(null).build();
        List<FError> err  = Validators.validate(s);
        MyAssert.assertContainsSingleError(err, "Séance null ou sans Id");
        
        s = sb.id("junit").build();
        err  = Validators.validate(s);
        MyAssert.assertSize(err, 3);
        MyAssert.assertContainsError(err, "Séance junit : classe obligatoire");
        MyAssert.assertContainsError(err, "Séance junit : journée obligatoire");
        MyAssert.assertContainsError(err, "Séance junit : cours obligatoire");
        
        s = sb.classe().journee().cours().build();
        err  = Validators.validate(s);
        MyAssert.assertEmpty(err);
        Eleve e1 = new EleveBuilder().firstName("eleve1").lastName("test").build();
        Eleve e2 = new EleveBuilder().firstName("eleve2").lastName("test").build();
        Eleve e3 = new EleveBuilder().firstName("eleve3").lastName("test").build();
        EleveData d1 = new DonneeBuilder().id("d1").eleve(e1).build();
        e1.getData().add(d1);
        EleveData d2 = new DonneeBuilder().id("d2").eleve(e2).build();
        EleveData d3 = new DonneeBuilder().id("d3").eleve(null).build();
        
        Classe classe = new ClasseBuilder().name("classeTest").eleve(e2).eleve(e3).build();
        s = sb
            .classe(classe)
            .donnee(null, new DonneeBuilder().eleve().build())
            .donnee(e1, d1)
            .donnee(e2, d2)
            .donnee(e3, d3)
            .build();
        
        err  = Validators.validate(s);
        MyAssert.assertSize(err,4);
        MyAssert.assertContainsError(err, "Séance junit : données présentes pour élève inexistant");
        MyAssert.assertContainsError(err, "Séance junit : l'Elève eleve1 test ne fait pas partie de la Classe classeTest");
        MyAssert.assertContainsError(err, "Séance junit : la Donnée Elève eleve2 test (d2) n'appartient pas à l'Elève eleve2 test");
        MyAssert.assertContainsError(err, "Séance junit : la Donnée (d3) n'appartient pas à l'Elève eleve3 test");
    }
    
    @Test
    public void seanceListValidatorsTest(){
        JourneeBuilder jb = new JourneeBuilder();
        Journee j = jb.date(TEST_DATE).build();
        j = jb
            .seance(null)
            .seance(new SeanceBuilder().id("junit").classe().cours().journee().build())
            .seance(new SeanceBuilder().id("junit2").classe().cours().journee(j).build())
            .build();
        List<FError> err  = ListValidators.validateSeanceList(j);
        MyAssert.assertContainsError(err, "Journée 2016-12-15 : Séance null");
        MyAssert.assertContainsError(err, "Journée 2016-12-15 : la Séance junit n'a pas la bonne journée");
    }
    
}
