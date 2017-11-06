package com.jiahaoliuliu.simplerealmproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jiahaoliuliu.simplerealmproject.model.Dog;
import com.jiahaoliuliu.simplerealmproject.model.Person;

import io.realm.DynamicRealm;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;

/**
 * Following this tutorial
 * https://realm.io/docs/java/3.5.0/
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleRealmProject";
    private static final int SCHEMA_VERSION = 2;

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
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(configuration);
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
        person = realm.createObject(Person.class, SCHEMA_VERSION); // Create managed objects directly
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

    public class MyMigration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            Log.d(TAG,"Migration called with dynamic Realm: " + realm + ", old version: " + oldVersion
                + ", new version: " + newVersion);

            if (oldVersion < 2) {
                migrateToVersion2(realm);
            }
        }

        private void migrateToVersion2(DynamicRealm realm) {
            RealmObjectSchema personSchema = realm.getSchema().get(Person.class.getSimpleName());
            addFieldIfNotExist(personSchema, "age", int.class);
            addFieldIfNotExist(personSchema, "isMan", boolean.class);
            Log.v(TAG, "Realm migrated to version 2");
        }
    }

    private void addFieldIfNotExist(RealmObjectSchema realmObjectSchema, String fieldName, Class<?> fieldType) {
        if (!realmObjectSchema.hasField(fieldName)) {
            realmObjectSchema.addField(fieldName, fieldType);
        }
    }
}