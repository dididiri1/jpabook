package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity","street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "20000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "20000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========= START ============");
            Member findMember = em.find(Member.class, member.getId()); // 지연로딩

            // homeCity -> newCity
            // findMember.getHomeAddress().setCity("newCity");
            //Address a = findMember.getHomeAddress();
            //findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));

            // 치킨 -> 한식
            //findMember.getFavoriteFoods().remove("치킨");
            //findMember.getFavoriteFoods().add("삽겹살");

            //findMember.getAddressHistory().remove(new Address("old1", "street", "20000"));
            //findMember.getAddressHistory().add(new Address("newOld", "street", "20000"));

            //findMember.getAddressHistory().remove(new AddressEntity("old1", "street", "20000"));
            //findMember.getAddressHistory().add(new AddressEntity("newOld", "street", "20000"));

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
