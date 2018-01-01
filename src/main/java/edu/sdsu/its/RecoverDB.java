package edu.sdsu.its;

import org.mapdb.DBMaker;

/**
 * Run this if the DB Checksums have become corrupted as a result of aborting a Course List Sync.
 *
 * @author Tom Paulus
 * Created on 12/30/17.
 */
public class RecoverDB {
    public static void main(String[] args) {
        org.mapdb.DB db = DBMaker
                .fileDB("courses.db")
                .transactionEnable()
                .checksumHeaderBypass()
                .make();

        db.commit();
        db.close();
    }
}
