package hellojpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") // 엔티티명이 아니고 다른걸로 바꾸고 싶을때 씀.
public class Album extends Item{

    private String artist;
}
