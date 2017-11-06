package com.jiahaoliuliu.simplerealmproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jiahaoliuliu.simplerealmproject.model.Dog;
import com.jiahaoliuliu.simplerealmproject.model.Person;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleRealmProject";

    private Context context;

    // Models
    private Dog dog;
    private Dog managedDog;
    private Person person;

    // Realm
    private Realm realm;
    private RealmResults<Dog> puppies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        setValues();

        // Init Realm
        initRealm();
        puppiesQuery();
        persistTheData();
        setListener();
        updateObjectOnBackground();
    }

    private void setValues() {
        dog = new Dog();
        dog.setName("Rex");
        dog.setAge(1);
    }

    private void initRealm() {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    private void puppiesQuery() {
        puppies = realm.where(Dog.class)
                .lessThan("age", 2).findAll();
        Log.v(TAG, "Quering the number of puppies " + puppies.size());
    }

    private void persistTheData() {
        realm.beginTransaction();
        managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
        person = realm.createObject(Person.class); // Create managed objects directly
        person.getDogs().add(managedDog);
        realm.commitTransaction();
    }

    private void setListener() {
        // Listener will be notified when data changes
        puppies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> dogs, OrderedCollectionChangeSet changeSet) {
                // Query results are updated in real time with fine grained notifications
                changeSet.getInsertions(); // => [0] is added
            }
        });
    }

    private void updateObjectOnBackground() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Dog dog = bgRealm.where(Dog.class).equalTo("age", 1).findFirst();
                dog.setAge(3);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Original queries and Realm objects are automatically updated
                puppies.size();
                managedDog.getAge(); // 3 the dog age is updated
                Log.d(TAG, "The managed dog age is " + managedDog.getAge());
            }
        });
    }
}