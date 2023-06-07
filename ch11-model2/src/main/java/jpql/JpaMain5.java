package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;


public class JpaMain5 {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            member1.setAge(0);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            member2.setAge(0);
            em.persist(member2);

            Member member3= new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            member3.setAge(0);
            em.persist(member3);

            //em.flush();
            //em.clear();

            // 벌크 연산
            // em.flush() 자동 호출 commit, query
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount = " + resultCount);

            // DB는 나이가 20인데 영속성 데이터는 아직 0임
            System.out.println("member1 = " + member1.getAge());
            System.out.println("member2 = " + member2.getAge());
            System.out.println("member3 = " + member3.getAge());

            em.clear(); // 클리어 하면 영속컨테스트에 없으니깐 DB에서 새로 조회함 그래서 영속성 컨텍스트만 em.clear() 하면 됨.

            Member findMember = em.find(Member.class, member1.getId());

            System.out.println("findMember.getAge() = " + findMember.getAge());

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
