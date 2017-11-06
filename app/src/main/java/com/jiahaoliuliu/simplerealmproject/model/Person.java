package com.jiahaoliuliu.simplerealmproject.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jiahaoliu on 11/5/17.
 */
public class Person extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    private RealmList<Dog> dogs;
    private int age;
    private boolean isMan;

    public Person() {
    }

//    public Person(long id, String name, RealmList<Dog> dogs) {
//        this.id = id;
//        this.name = name;
//        this.dogs = dogs;
//    }


    public Person(long id, String name, RealmList<Dog> dogs, int age, boolean isMan) {
        this.id = id;
        this.name = name;
        this.dogs = dogs;
        this.age = age;
        this.isMan = isMan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(RealmList<Dog> dogs) {
        this.dogs = dogs;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMan() {
        return isMan;
    }

    public void setMan(boolean man) {
        isMan = man;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Person person = (Person) o;
//
//        if (id != person.id) return false;
//        if (name != null ? !name.equals(person.name) : person.name != null) return false;
//        return dogs != null ? dogs.equals(person.dogs) : person.dogs == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (name != null ? name.hashCode() : 0);
//        result = 31 * result + (dogs != null ? dogs.hashCode() : 0);
//        return result;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != person.id) return false;
        if (age != person.age) return false;
        if (isMan != person.isMan) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        return dogs != null ? dogs.equals(person.dogs) : person.dogs == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dogs != null ? dogs.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (isMan ? 1 : 0);
        return result;
    }

//    @Override
//    public String toString() {
//        return "Person{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", dogs=" + dogs +
//                '}';
//    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dogs=" + dogs +
                ", age=" + age +
                ", isMan=" + isMan +
                '}';
    }
}
