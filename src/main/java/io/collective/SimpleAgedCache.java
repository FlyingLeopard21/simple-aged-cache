package io.collective;

import java.time.Clock;

public class SimpleAgedCache {
    ExpirableEntry head;
    ExpirableEntry tail;
    int lengthOfCacheList;
    Clock clock;

    class ExpirableEntry{
        ExpirableEntry previousEntry;
        ExpirableEntry nextEntry;
        Object key;
        Object value;
        long expiringTime;

        // Constructor for ExpirableEntry class
        public ExpirableEntry(){
            this.previousEntry = null;
            this.nextEntry = null;
        }

        // Constructor for ExpirableEntry class
        public ExpirableEntry(Object key, Object value, int retentioninMillis, Clock clock){
            this.previousEntry = null;
            this.nextEntry = null;
            this.key = key;
            this.value = value;
            this.expiringTime = clock.millis() + retentioninMillis;
        }

    }

    public SimpleAgedCache(Clock clock) {
        this.head = null;
        this.tail = null;
        this.lengthOfCacheList = 0;
        this.clock = clock;

    }

    public SimpleAgedCache() {
        this.head = null;
        this.tail = null;
        this.lengthOfCacheList = 0;
        this.clock = Clock.systemUTC();

    }

    public void put(Object key, Object value, int retentionInMillis) {
        // check if SimpleAgedCache isEmpty.
        // if yes, then create an ExpirableEntry object and add that as the head
        // if not, then create an ExpirableEntry object and add that to the end of the list
        ExpirableEntry newEntry = new ExpirableEntry(key, value, retentionInMillis, this.clock);
        if(this.isEmpty()){
            this.head = newEntry;
            this.tail = newEntry;
            this.lengthOfCacheList += 1;
        }
        else{
            this.tail.nextEntry = newEntry;
            newEntry.previousEntry = this.tail;
            this.tail = newEntry;
            this.lengthOfCacheList += 1;
        }

    }

    public boolean isEmpty() {

        if (lengthOfCacheList == 0){
            return true;
        }
        else{
            return false;
        }
    }

    public int size() {
        RemoveExpired();
        return this.lengthOfCacheList;
    }

    public Object get(Object key) {
        RemoveExpired();
        if(!(this.isEmpty())){
            // Remove expired entries from the list
            // Start at the head
            ExpirableEntry currEntry = new ExpirableEntry();
            currEntry = this.head;
            // Traverse the list until tail.next == NULL
            int entryNum = 0;
            // Compare key value against value in ExpirableEntry object
            while((currEntry.key != key) && (entryNum != this.lengthOfCacheList)){
                currEntry = currEntry.nextEntry;
                entryNum++;
            }
            return currEntry.value;
        }
        else{
            return null;
        }
    }

    public void RemoveExpired(){
        if(!(this.isEmpty())){
            ExpirableEntry currEntry = new ExpirableEntry();
            currEntry = this.head;
            // Traverse the list until tail.next == NULL
            int entryNum = 0;
            boolean isExpired = false;
            // Compare key value against value in ExpirableEntry object
            while(entryNum != this.lengthOfCacheList){
                isExpired = CheckTimer(currEntry.expiringTime);
                if(isExpired){
                    if (currEntry == this.head){
                        this.head = currEntry.nextEntry;
                        this.head.previousEntry = null;
                    }
                    else if (currEntry == this.tail) {
                        this.tail = currEntry.previousEntry;
                        this.tail.nextEntry = null;
                    }
                    else{
                        currEntry = currEntry.nextEntry;
                        currEntry.previousEntry = currEntry.previousEntry.previousEntry;
                    }
                    this.lengthOfCacheList--;
                }
                currEntry = currEntry.nextEntry;
                entryNum++;
            }
        }
    }

    public boolean CheckTimer(long expiringTime){
        return (this.clock.millis() > expiringTime);
    }


}