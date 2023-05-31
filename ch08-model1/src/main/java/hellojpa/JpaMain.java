package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{

            Member member1 = new Member();
            member1.setUsername("member1");

            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");

            em.persist(member2);

            em.flush();
            em.clear();
            
            // Member findMember = em.find(Member.class, member.getId());

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.getReference(Member.class, member2.getId());
            System.out.println("findMember = " + m1.getClass());
            System.out.println("findMember.getId() = " + m1.getId());
            System.out.println("findMember.getUsername() = " + m1.getUsername());

            System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));

            logic(m1, m2);


            Member member3 = new Member();
            member3.setUsername("member3");
            em.persist(member3);

            Member m3 = em.find(Member.class, member3.getId());
            System.out.println("m3.getClass() = " + m3.getClass());

            Member reference = em.getReference(Member.class, member3.getId());
            System.out.println("reference.getClass() = " + reference.getClass());

            System.out.println("a == a: " + (m1 == reference));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace(); //이 부분을 추가하면 예외를 확인할 수 있습니다.
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(Member m1, Member m2) {
        System.out.println("m1 == m2 : " + (m1 instanceof Member));
        System.out.println("m1 == m2 : " + (m2 instanceof Member));
    }
}
