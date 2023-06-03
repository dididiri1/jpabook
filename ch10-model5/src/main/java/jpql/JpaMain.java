package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;


public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);

            em.persist(member);

            // 1.
            //String query = "select m from Member m inner join m.team t";

            // 2.
            String query = "select m from Member m left join m.team t on t.name = 'teamA'";

            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            System.out.println("result.size() = " + result.size());

            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace(); //이 부분을 추가하면 예외를 확인할 수 있습니다.
        } finally {
            em.close();
        }

        emf.close();
    }


}
