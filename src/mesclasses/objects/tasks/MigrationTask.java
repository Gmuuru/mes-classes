/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mesclasses.handlers.DonneesHandler;
import mesclasses.handlers.ModelHandler;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Classe;
import mesclasses.model.Constants;
import mesclasses.model.Cours;
import mesclasses.model.Eleve;
import mesclasses.model.EleveData;
import mesclasses.model.Journee;
import mesclasses.model.Mot;
import mesclasses.model.Seance;
import mesclasses.util.DataLoadUtil;
import mesclasses.util.FileSaveUtil;
import mesclasses.util.NodeUtil;
import mesclasses.util.validation.FError;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rrrt3491
 */
public class MigrationTask extends AppTask<Object> {

    private static final Logger LOG = LogManager.getLogger(MigrationTask.class);

    private ModelHandler handler;
    private String currentVersion;
    private final String patchVersion = "1.0.1";

    private int nbJournees;
    private int nbCoursPonctuels;
    private int nbSeances;
    private int nbDonnees;
    private int nbPunitions;

    private int nbDonneesInitiales;
    private int nbPunitionsInitiales;

    public MigrationTask() {
        super();
        updateProgress(0, 5.0);
    }

    @Override
    public Object call() throws Exception {
        handler = ModelHandler.getInstance();
        currentVersion = PropertiesCache.version();
        LOG.info("Current version is " + currentVersion + ", migrating to " + patchVersion);
        process();
        return null;
    }

    private void process() throws Exception {

        try {
            if (handler.getTrimestres().isEmpty()) {
                return;
            }
            getInitialStats();
            LocalDate day = handler.getTrimestres().get(0).getStartAsDate();
            long nbDays = ChronoUnit.DAYS.between(day, LocalDate.now());
            while (day.isBefore(LocalDate.now().plusDays(1))) {

                if (currentVersion.compareTo("1.0.0") == 0) {
                    Journee journee = createJournee(day);
                    handler.declareJournee(journee);
                } else if (!handler.getJournees().containsKey(day)) {
                    Journee journee = createJournee(day);
                    handler.declareJournee(journee);
                }
                nbJournees++;
                day = day.plusDays(1);
                updateProgress(nbJournees, nbDays);
            }

            if (currentVersion.compareTo("1.0.0") == 0) {
                updatePunitions();
                updateDevoirsEtMots();
            }

            if (!checkData()) {
                LOG.error("Certaines données sont incohérentes");
                throw new Exception("Certaines données sont incohérentes");
            } else {
                saveConfAndData();

            }
        } catch (Exception e) {
            LOG.error("MigrationException", e);
            setMsg("Impossible d'effectuer la conversion en séances : " + e.getMessage());
            throw e;
        }
        LOG.info("nbDonneesInitiales : " + nbDonneesInitiales + ", nbPunitionsInitiales : " + nbPunitionsInitiales);
        LOG.info("journees : " + nbJournees + ", seances " + nbSeances + ", cours additionnels : "
                + nbCoursPonctuels + ", donnees traitées : " + nbDonnees + ", punitions traitées : " + nbPunitions);
    }

    private void saveConfAndData() {
        //sauvegarde de la nouvelle version
        PropertiesCache.getInstance().setProperty(Constants.CONF_VERSION, patchVersion);
        PropertiesCache.getInstance().save();
        
        //sauvegarde des données dans le fichier de sauvegarde
        DataLoadUtil.writeData(handler.getData(), FileSaveUtil.getSaveFile());
    }

    public Journee createJournee(LocalDate date) {
        if (date == null) {
            return null;
        }
        Journee journee = new Journee();
        journee.setDate(date);
        handler.getClasses().forEach(c -> {
            buildSeancesForClasse(journee, c, date);
        });
        Collections.sort(journee.getCoursPonctuels());
        Collections.sort(journee.getSeances());
        return journee;
    }

    private boolean checkData() {
        final List<FError> errList = handler.getData().validate();
        if (!errList.isEmpty()) {
            LOG.error("\n[ " + StringUtils.join(errList, " ]\n[ ") + " ]");
            return false;
        }
        return true;
    }

    private void addDonneesToSeance(List<EleveData> donnees, Seance seance) {
        donnees.forEach(d -> {
            LOG.info(d.getDisplayName() + " ajoutée à la séance " + seance.getDisplayName());
            d.getEleve().getData().remove(d);
            d.setSeance(seance);
            DonneesHandler.getInstance().persistEleveData(d);
        });
    }

    private void buildSeancesForClasse(Journee journee, Classe classe, LocalDate date) {
        try {
            LOG.debug("Journée " + journee.getDate() + ", Séances pour la classe " + classe);
            // liste des cours pour la classe à cette date
            List<Cours> listeCours = handler.getCoursForDateAndClasse(date, classe);
            // map des données élèves, réparties par cours
            Map<Integer, List<EleveData>> donneesParCours = getDataParCoursForDate(classe, date);

            if (listeCours.size() < donneesParCours.size()) {
                LOG.debug("Cours trouvés pour la " + classe + " le " + NodeUtil.getJour(date) + " :");
                LOG.debug("\n" + StringUtils.join(listeCours, "\n"));
                LOG.debug("Journée du " + journee.getDate() + " : " + listeCours.size() + " cours prévus, " + donneesParCours.size() + " cours trouvés");
                LOG.debug("Index des cours : " + StringUtils.join(donneesParCours.keySet(), ", "));
            }
            //cours normaux
            for (int index = 0; index < listeCours.size(); index++) {
                Seance seance = handler.addSeanceWithCours(journee, listeCours.get(index));
                if (donneesParCours.containsKey(index + 1)) {
                    LOG.debug("Création de séance pour le cours " + (index + 1));
                    addDonneesToSeance(donneesParCours.get(index + 1), seance);
                    nbDonnees += donneesParCours.get(index + 1).size();
                }
            }
            //cours 0 ?
            if (donneesParCours.containsKey(0)) {
                LOG.debug("Création de séance ponctuelle pour le cours " + (0));
                Seance seance = handler.addSeanceWithCoursPonctuel(journee, classe);
                addDonneesToSeance(donneesParCours.get(0), seance);
                nbDonnees += donneesParCours.get(0).size();
            }

            // cours ponctuels
            for (int index = listeCours.size(); index < donneesParCours.size(); index++) {
                if (!donneesParCours.containsKey(index + 1)) {
                    break;
                }
                LOG.debug("Création de séance ponctuelle pour le cours " + (index + 1));
                Seance seance = handler.addSeanceWithCoursPonctuel(journee, classe);
                addDonneesToSeance(donneesParCours.get(index + 1), seance);
                nbDonnees += donneesParCours.get(index + 1).size();
            }
            nbSeances += journee.getSeances().size();
        } catch (Exception e) {
            LOG.error("erreur dans la construction de séances", e);
            setMsg(e.getMessage());
        }
    }

    private Map<Integer, List<EleveData>> getDataParCoursForDate(Classe classe, LocalDate date) {
        Map<Integer, List<EleveData>> res = new HashMap<>();
        if (classe == null || classe.getEleves() == null || classe.getEleves().isEmpty()) {
            return new HashMap<>();
        }
        for (Eleve eleve : classe.getEleves()) {
            for (EleveData eleveData : eleve.getData()) {
                if (eleveData.getDateAsDate().isEqual(date)) {
                    if (!res.containsKey(eleveData.getCours())) {
                        res.put(eleveData.getCours(), new ArrayList<>());
                    }
                    res.get(eleveData.getCours()).add(eleveData);
                }
            }
        }
        return res;
    }

    @Override
    public String getName() {
        return "Migration";
    }

    private void getInitialStats() {
        handler.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                nbDonneesInitiales += e.getData().size();
                nbPunitionsInitiales += e.getPunitions().size();
            });
        });
    }

    private void updatePunitions() {
        handler.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                e.getPunitions().forEach(p -> {
                    Journee journee = handler.getJournee(p.getDateAsDate());
                    if (journee == null) {
                        LOG.error("erreur : punition " + p.getId() + " n'a pas de journée associée...");
                        return;
                    }
                    List<Seance> seances = journee.getSeances().filtered(s -> s.getClasse().getName().equals(p.getEleve().getClasse().getName()));
                    if (seances == null || seances.isEmpty()) {
                        LOG.error("erreur : punition " + p.getId() + " n'a pas de séance associée...");
                        return;
                    }
                    p.setSeance(seances.get(0));
                    nbPunitions++;
                });
            });
        });
    }

    /**
     * inscrit l'élève associé dans chaque donnée
     */
    private void updateDevoirsEtMots() {
        handler.getClasses().forEach(c -> {
            c.getEleves().forEach(e -> {
                List<Mot> motsAVerifier = new ArrayList<>();
                e.getData().forEach(d -> {
                    if (d.isDevoir() && handler.getDevoirForSeance(e, d.getSeance()) == null) {
                        LOG.debug("devoir pour {}, séance {}", e, d.getSeance());
                        handler.createDevoir(e, d.getSeance());
                        d.setDevoir(false);
                    }
                    if (d.isMotCarnet() && handler.getMotForSeance(e, d.getSeance()) == null) {
                        LOG.debug("mot pour {}, séance {}", e, d.getSeance());
                        Mot mot = handler.createMot(e, d.getSeance());
                        d.setMotCarnet(false);
                        motsAVerifier.add(mot);
                    }
                    if (d.isMotSigne()) {
                        LOG.debug("mot signé pour {}, séance {}", e, d.getSeance());
                        if (!motsAVerifier.isEmpty()) {
                            Mot mot = motsAVerifier.get(0);
                            mot.setDateCloture(d.getSeance().getDateAsDate());
                            motsAVerifier.remove(mot);
                        }
                        d.setMotSigne(false);
                    }
                });
            });
        });
    }

}
