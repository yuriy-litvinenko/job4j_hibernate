import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

public class HbmRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            session.createQuery("insert into Candidate (name, experience, salary) " +
                            "select 'Steve Jobs', 40, 20000000 from Candidate " +
                            "where id = (select max(id) from Candidate)")
                    .executeUpdate();
            session.createQuery("insert into Candidate (name, experience, salary) " +
                            "select 'Bill Gates', 50, 30000000 from Candidate " +
                            "where id = (select max(id) from Candidate)")
                    .executeUpdate();
            session.createQuery("insert into Candidate (name, experience, salary) " +
                            "select 'Linus Torvalds', 45, 10000000 from Candidate " +
                            "where id = (select max(id) from Candidate)")
                    .executeUpdate();

            Query query;

            query = session.createQuery("from Candidate");
            for (Object cn : query.list()) {
                System.out.println(cn);
            }

            query = session.createQuery("from Candidate s where s.id = :fId");
            query.setParameter("fId", 12);
            System.out.println(query.uniqueResult());

            query = session.createQuery("from Candidate s where s.name = :fName");
            query.setParameter("fName", "Bill Gates");
            System.out.println(query.getResultList());

            query = session.createQuery(
                    "update Candidate s set s.name = :newName, s.experience = :newExperience, s.salary = :newSalary " +
                            "where s.id = :fId");
            query.setParameter("newName", "Steven Ballmer")
                    .setParameter("newExperience", 30)
                    .setParameter("newSalary", 25000000)
                    .setParameter("fId", 13)
                    .executeUpdate();

            session.createQuery("delete from Candidate where id = :fId")
                    .setParameter("fId", 12)
                    .executeUpdate();

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
