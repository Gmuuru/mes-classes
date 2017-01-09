/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesclasses.objects.tasks;

import java.util.List;
import mesclasses.handlers.ModelHandler;
import mesclasses.handlers.PropertiesCache;
import mesclasses.model.Constants;
import mesclasses.util.DataLoadUtil;
import mesclasses.util.FileSaveUtil;
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

    private boolean checkData() {
        final List<FError> errList = handler.getData().validate();
        if (!errList.isEmpty()) {
            LOG.error("\n[ " + StringUtils.join(errList, " ]\n[ ") + " ]");
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "Migration";
    }

}
