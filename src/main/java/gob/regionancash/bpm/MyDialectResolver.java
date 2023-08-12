package gob.regionancash.bpm;

import org.hibernate.dialect.Database;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public class MyDialectResolver implements DialectResolver {

    public Dialect resolveDialect(DialectResolutionInfo info) {
        for (Database database : Database.values()) {
            /*Dialect dialect = database.resolveDialect(info);
            if (dialect != null) {
                dialect.getKeywords().add("separator");
                return dialect;
            }*/
        }

        return null;
    }
    
}