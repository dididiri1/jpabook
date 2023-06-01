package jpql;

import javax.persistence.*;
import java.util.List;


public class JpaMain4 {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // 영속성 컨텍스트에서 관리 된다.
            List<Member> result = em.createQuery("select m from Member m ", Member.class).getResultList();

            Member findMember = result.get(0);
            findMember.setAge(20);


            List<Team> result2 = em.createQuery("select m.team from Member m join m.team t", Team.class).getResultList();

            em.createQuery("select o.address from Order o", Address.class).getResultList();


            em.createQuery("select distinct m.username, m.age from Member m").getResultList();

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
