package edu.sdsu.its;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class DB {
    private static DB instance = new DB();
    private static Gson gson = new Gson();
    private static org.mapdb.DB db;

    private DB() {
        db = DBMaker
                .fileDB("courses.db")
                .transactionEnable()
                .make();
    }

    public static DB getInstance() {
        return instance;
    }

    private ConcurrentMap<String, String> getMap() {
        return db
                .hashMap("map", Serializer.STRING, Serializer.STRING)
                .createOrOpen();
    }

    public String get(String key) {
        return getMap().get(key);
    }

    public void put(String key, Object value) {
        getMap().put(key, gson.toJson(value));
        db.commit();
    }

    public void clear() {
        getMap().clear();
        db.commit();
    }

    public int size() {
        return getMap().size();
    }

    public Set<String> keySet() {
        return getMap().keySet();
    }
}
