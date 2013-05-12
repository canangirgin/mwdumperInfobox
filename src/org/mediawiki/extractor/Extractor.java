package org.mediawiki.extractor;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class Extractor {



    abstract String Extract(Template template) throws SQLException;





}
