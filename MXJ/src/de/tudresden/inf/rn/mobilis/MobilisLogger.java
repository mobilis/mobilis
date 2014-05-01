package de.tudresden.inf.rn.mobilis;

import java.util.logging.Logger;

/**
 * @author cmdaltent
 */
public class MobilisLogger {

    public static Logger getLogger() {
        return Logger.getLogger("de.tudresden.inf.rn.mobilis");
    }

    public static Logger getHibernateLogger() {
        return Logger.getLogger("org.hibernate");
    }

}
