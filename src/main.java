
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Julian Esteban Solarte Rivera - Universidad del Cauca
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Desde cualquier lado se puede modificar las variables al gusto
        Map properties = new HashMap();        
        properties.put("javax.persistence.jdbc.driver", "oracle.jdbc.OracleDriver");
        properties.put("javax.persistence.jdbc.url", "jdbc:oracle:thin:@localhost:1521:XE");
        properties.put("javax.persistence.jdbc.user", "Administrador");
        properties.put("javax.persistence.jdbc.password", "oracle");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaAcademicoPU",properties);
        EntityManager em = emf.createEntityManager();
        //Se hace una consulta normal y se retornan los resultados
        //Al igual que se puede almancernar una consulta nativa en un modelo
        //y llamarla desde cualquier controlados
        try {
            Query q = em.createNativeQuery("SELECT * FROM Persona");
            List<Object[]> personas = q.getResultList();
            for (Object[] a : personas) {
                System.out.println("Author "
                        + a[0]
                        + " "
                        + a[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

    }

}
