import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Arrays;

public class HashedDictionary<K extends Comparable<? super K>, V> implements DictionaryInterface<K, V> {
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 5; // Must be prime

    // The hash table:
    private TableEntry<K, V>[] hashTable;
    private int primeSize;                         // Must be prime
    private int collisionCount;
    private boolean checkHashFunction,checkCollision;
    private  double MAX_LOAD_FACTOR ; // Fraction of hash table
    // that can be filled



    public HashedDictionary()
    {
        this(DEFAULT_CAPACITY); // Call next constructor
    } // end default constructor

    public HashedDictionary(int initialCapacity)
    {
        numberOfEntries = 0;    // Dictionary is empty
        int tableSize = getNextPrime(initialCapacity);
        primeSize= getPrime(initialCapacity); // less than table size for double hashing

        @SuppressWarnings("unchecked")
        TableEntry<K, V>[] temp = (TableEntry<K, V>[])new TableEntry[tableSize];
        hashTable = temp;
        checkHashFunction = false;
        checkCollision=false;
        collisionCount=0;
        MAX_LOAD_FACTOR = 0.5;
    }
    public V put(K key, V value){
        if ((key == null) || (value == null))
            throw new IllegalArgumentException();
        else{

            V oldValue;
            int index = getHashIndex(key);
            index = collisionHandling(index, key); // Check for and resolve collision
            assert (index >= 0) && (index < hashTable.length);

            if ( (hashTable[index] == null) || hashTable[index].isRemoved()) { // Key not found, so insert new entry
                hashTable[index] = new TableEntry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            }
            else{// Key found; get old value for return and then replace it
                oldValue = hashTable[index].getValue();
                hashTable[index].setValue(value);
            }
            if (isHashTableTooFull())
                resize();
            return oldValue;
        }
    }
    public V remove(K key){
        V removedValue=null;
        int index=getHashIndex(key);
        index=locate(index,key);
        if(index !=-1){
            removedValue=hashTable[index].getValue();
            hashTable[index].setToRemoved();
            numberOfEntries--;
        }
        return removedValue;
    }

    private int collisionHandling(int index, K key)
    {
        boolean found = false;
        int removedStateIndex = -1;// Index of first element in available state
        while ( !found && (hashTable[index] != null) )
        {
            if (hashTable[index].isIn())
            {
                if (key.equals(hashTable[index].getKey()))
                    found = true; // Key found
                else{
                    if(checkCollision){
                        index = (index + 1) % hashTable.length; // Linear probing
                    }
                    else{
                        index= doubleHashing(key);
                    }
                    collisionCount++;
                }
            }
            else // Element in available state; skip it, but mark the first one encountered
            {
                // Save index of first element in available state
                if (removedStateIndex == -1)
                removedStateIndex = index;
                if(checkCollision){
                    index = (index + 1) % hashTable.length; // Linear probing
                }
                else{
                    index= doubleHashing(key);
                }
                collisionCount++;
            }
        }
        if (found || (removedStateIndex ==-1))
            return index;// Index of either key or null
        else
            return removedStateIndex; // Index of an available element
    }
    private int locate(int index, K key) {
        boolean found = false;
        while ( !found && (hashTable[index] != null) ) {
            if ( hashTable[index].isIn() && key.equals(hashTable[index].getKey()) )
                found = true;
            else{
                if(checkCollision){
                    index = (index + 1) % hashTable.length; // Linear probing
                }
                else{
                    index= doubleHashing(key);
                }
            }
        }
        int result = -1;
        if (found)
            result = index;
        return result;
    }

    public V getValue(K key)
    {
        V result = null;
        int index = getHashIndex(key);
        index= locate(index,key);
        if (index != -1)
            result = hashTable[index].getValue(); // Key found; get value
        return result;
    }
    private int getHashIndex(K key)
    {
        int hashIndex;
        if(checkHashFunction){
            hashIndex= SSF(key);
        }
        else{
            hashIndex = PAF(key);
        }
        if (hashIndex < 0)
            hashIndex = hashIndex + hashTable.length;
        return hashIndex;
    }

    private boolean isPrime(int num){
        if(num <=1){
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num) ; i++) {
            if(num % i==0){
                return false;
            }

        }
        return true;
    }
    private int getNextPrime(int anInteger){
        while(!isPrime(anInteger)){
            anInteger++;
        }
        return anInteger;
    }

    // -----Double Hashing--------

    private  int hash1(K s){
        int hashValue;
        if(checkHashFunction){
            hashValue= SSF(s);
        }
        else{
            hashValue = PAF(s);
        }
        hashValue %= hashTable.length;
        if(hashValue <0){
            hashValue += hashTable.length;
        }
        return hashValue;
    }
    private int hash2(K s){
        int hashValue;
        if(checkHashFunction){
            hashValue= SSF(s);
        }
        else{
            hashValue = PAF(s);
        }
        hashValue %= hashTable.length;
        if(hashValue <0){
            hashValue += hashTable.length;
        }
        return primeSize - hashValue % primeSize;
    }
    private int getPrime(int num){
        for (int i = num -1; i >1; i--) {
            if(isPrime(i)){
                return i;
            }
        }
        return -1;
    }
    private int doubleHashing(K key){
        int hash1= hash1(key);
        int hash2= hash2(key);
        while(hashTable[hash1] != null && !hashTable[hash1].getKey().equals(key)){
            hash1 += hash2;
            hash1 %= hashTable.length;
        }
        return hash1;
    }
    private int SSF(K s){
        char[] arr=String.valueOf(s).toCharArray();
        int sum=0;
        for(char c: arr){
            sum += (c- 'a' +1);

        }
        return sum % hashTable.length;
    }
    private int PAF(K s){
        char[] arr=String.valueOf(s).toCharArray();
        int sum=0;
        int index =1;
        int valueOfChar;
        int z= 33;
        for (char c: arr){
            valueOfChar =(c- 'a' +1);
            sum+=(valueOfChar * Math.pow(z,arr.length - index));
            index++;
        }
        return sum % hashTable.length;
    }

    private void resize()
    {
        TableEntry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize + oldSize);

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        TableEntry<K, V>[] temp = (TableEntry<K, V>[])new TableEntry[newSize];
        hashTable = temp;
        numberOfEntries = 0;
        for (int index = 0; index < oldSize; index++)
        {
            if ( (oldTable[index] != null) && oldTable[index].isIn() ) //  && oldTable[index] != AVAILABLE
                put(oldTable[index].getKey(), oldTable[index].getValue());
        }
    }
    private static class TableEntry<S, T> {
        private S key;
        private T value;
        private States state;
        private enum States {CURRENT, REMOVED};

        private TableEntry(S key, T value) {
            this.key = key;
            this.value = value;
            state = States.CURRENT;

        }
        public boolean isIn(){
            if(state == States.CURRENT){
                return true;
            }
            return false;

        }
        public boolean isRemoved(){
            if(state == States.REMOVED){
                return true;
            }
            return false;
        }
        public void setToIn(){
            state=States.CURRENT;
        }
        public void setToRemoved(){
            state=States.REMOVED;
        }


        public S getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }


    }
    private class KeyIterator implements Iterator<K>
    {
        private int currentIndex; // Current position in hash table
        private int numberLeft;   // Number of entries left in iteration

        private KeyIterator()
        {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        public boolean hasNext()
        {
            return numberLeft > 0;
        } // end hasNext

        public K next()
        {
            K result = null;

            if (hasNext())
            {
                // Skip table locations that do not contain a current entry
                while ( (hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved())
                {
                    currentIndex++;
                } // end while

                result = hashTable[currentIndex].getKey();
                numberLeft--;
                currentIndex++;
            }
            else
                throw new NoSuchElementException();

            return result;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    }

    public boolean isHashTableTooFull(){
        int loadFactor= numberOfEntries/hashTable.length;
        if(loadFactor >= MAX_LOAD_FACTOR){
            return true;
        }
        return false;
    }
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }
    public int getSize() {
        return numberOfEntries;
    }
    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }
    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    }
    private class ValueIterator implements Iterator<V> {
        private Iterator<TableEntry<K, V>> traverser;
        // end default constructor
        private ValueIterator() {
            TableEntry<K, V>[] tempDictionary = (TableEntry<K, V>[]) new TableEntry[numberOfEntries];
            for(int i = 0; i < numberOfEntries; i++) {
                tempDictionary[i] = hashTable[i];
            }
            traverser = Arrays.asList(tempDictionary).iterator();
        }
        public boolean hasNext() {
            return traverser.hasNext();
        } // end hasNext

        public V next() {
            TableEntry<K, V> nextEntry = traverser.next();
            return (V) nextEntry.getValue();
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    }
    public void clear() {
        for(int i = 0; i < numberOfEntries; i++) {
            hashTable[i] = null;
        }
        numberOfEntries = 0;
    }
    public int getCollisionCount() {
        return collisionCount;
    }

    public  void setMaxLoadFactor(double maxLoadFactor) {
        MAX_LOAD_FACTOR = maxLoadFactor;
    }

    public void setCheckHashFunction(boolean checkHashFunction) {
        this.checkHashFunction = checkHashFunction;
    }
    public boolean isCheckHashFunction() {
        return checkHashFunction;
    }

    public void setCheckCollision(boolean checkCollision) {
        this.checkCollision = checkCollision;
    }

    public boolean isCheckCollision() {
        return checkCollision;
    }
}
