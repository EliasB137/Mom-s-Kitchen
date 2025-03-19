package il.cshaifasweng.OCSFMediatorExample.server;

import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;


/**
 * Hello world!
 *
 */

public class App 
{

	private static MomServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new MomServer(3000);
        server.listen();
    }
}
