package eu.unifiedviews.helpers.cuni.rdf.sparql;

import eu.unifiedviews.helpers.dataunit.HelperFailedException;

/**
 *
 * @author Škoda Petr
 */
public class SparqlProblemException extends HelperFailedException {

    public SparqlProblemException(String message) {
        super(message);
    }

    public SparqlProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SparqlProblemException(Throwable cause) {
        super(cause);
    }

}
