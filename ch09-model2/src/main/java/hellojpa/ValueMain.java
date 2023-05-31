package hellojpa;

public class ValueMain {

    public static void main(String[] args) {

        // 기본 타입(primitive type)
        int a = 10;
        int b = a; // 기본 타입은 값을 복사
        b = 4;

        System.out.println("a = " + a);
        System.out.println("b = " + b);


        // 객체 타입
        Address addressA = new Address("city", "street", "zipcode");
        Address addressB = addressA; // 객체 타입은 참조를 전달
        addressB.setCity("newCity");

        // 참조를 넘겨서 수정하면 값이 영향이 있음
        System.out.println("addressA = " + addressA.getCity());
        System.out.println("addressB = " + addressB.getCity());
    }
}
