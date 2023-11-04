import java.io.*;
import java.util.*;
import java.lang.*;
public class Main {

    public static LinkedList<String> removeStopWords(String[] arr, String[] stopWords){
        LinkedList<String> list = new LinkedList<>();
        for(String element: arr){
            if(element.equals(""))
                continue;
            if(Arrays.stream(stopWords).noneMatch(element::equalsIgnoreCase)){
                list.add(element);
            }
        }
        return list;
    }
    //inserts all words from temp to main data that is Data hashtable
    public static void insertTempHashToData(DictionaryInterface<String, Integer> temp,DictionaryInterface<String,LinkedList<FrequencyOfWords>> allData,String fileName){
        Iterator<String> keyIterator = temp.getKeyIterator();
        String key;
        while(keyIterator.hasNext()) {
            key = keyIterator.next();
            FrequencyOfWords fq = new FrequencyOfWords(fileName, temp.getValue(key));
            if (allData.getValue(key) == null) {
                LinkedList<FrequencyOfWords>  frequencyOfWordsLinkedList = new LinkedList<>();
                frequencyOfWordsLinkedList.add(fq);
                allData.put(key, frequencyOfWordsLinkedList);
            }
            else {
                allData.getValue(key).add(fq);
            }
        }
    }
    public static File[] finder(String dirName){
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".txt"); }
        } );
    }
    public static  void search(DictionaryInterface<String,LinkedList<FrequencyOfWords>> hash, String word){
        if(hash.getValue(word) != null){
            int size =hash.getValue(word).size();
            System.out.println(size + " documents found  ");
            for (int i = 0; i < size; i++) {
                System.out.println(hash.getValue(word).get(i).getFrequency() + "-" + hash.getValue(word).get(i).getFileName());
            }
        }
        else{
            System.out.println("Not found !");
        }

    }
    // I put start and end variables to end of while other one start of while then divided count variable to find avgTime
    // min values are always zero so ı show only max
    public static float searchTimeFromTxt(DictionaryInterface<String,LinkedList<FrequencyOfWords>> hashTable) throws Exception{
        BufferedReader bfr= null;
        long time;
        int count=0;
        float max=0;
        long start,end;
        Boolean found;
        String str;
        try {
            bfr = new BufferedReader(new FileReader("search.txt"));
            while((str = bfr.readLine()) != null){
                start = System.currentTimeMillis();
                if(hashTable.getValue(str) != null){
                    found=true;

                }
                else{
                    found=false;

                }
                end = System.currentTimeMillis();
                time=((end - start));
                if(found &&  time > max){
                    max=time;
                }
                count++;
            }

            bfr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return max;
    }
    public static void displayMenu(DictionaryInterface<String,LinkedList<FrequencyOfWords>> allData){
        Scanner sc= new Scanner(System.in);
        System.out.println("Please choose one of the following options");
        System.out.println("1- Load factor = 0.5 , SSF , LP");
        System.out.println("2- Load factor = 0.5 , PAF , LP");
        System.out.println("3- Load factor = 0.5 , SSF , DH");
        System.out.println("4- Load factor = 0.5 , PAF , DH");
        System.out.println("5- Load factor = 0.8 , SSF , LP");
        System.out.println("6- Load factor = 0.8 , PAF , LP");
        System.out.println("7- Load factor = 0.8 , SSF , DH");
        System.out.println("8- Load factor = 0.8 , PAF , DH");
        System.out.println("9- Exit");
        int choice ;

        System.out.println("Please choose one of the following options : ");
        choice = sc.nextInt();
        switch (choice){
            case 1:
                allData.setCheckCollision(true);
                allData.setCheckHashFunction(true);
                break;
            case 2:
                allData.setCheckCollision(true);
                break;
            case 3:
                allData.setCheckHashFunction(true);
                break;
            case 4:
            case 9:
                break;
            case 5:
                allData.setCheckCollision(true);
                allData.setCheckHashFunction(true);
                allData.setMaxLoadFactor(0.8);
                break;
            case 6:
                allData.setCheckCollision(true);
                allData.setMaxLoadFactor(0.8);
                break;
            case 7:
                allData.setCheckHashFunction(true);
                allData.setMaxLoadFactor(0.8);
                break;
            case 8:
                allData.setMaxLoadFactor(0.8);
                break;
            default:
                System.out.println("Choice must be a value between 1 and 9.");
        }


    }
    public static void main( String[] args) throws Exception{
        StringBuilder stopWords= new StringBuilder();
        DictionaryInterface<String,LinkedList<FrequencyOfWords>>  data = new HashedDictionary<>();
        BufferedReader br= new BufferedReader(new FileReader("stop_words_en.txt"));
        String str;
        while((str = br.readLine()) != null ){
            if (!str.equals(""))
                stopWords.append(str).append(" ");
        }
        br.close();
        String[] stopWord = stopWords.toString().split(" ");

        File dir = new File("bbc");
        ArrayList<TxtFile> txtFiles = new ArrayList<>();
        File[] folderNames = dir.listFiles();
        String fileName;

        for (File parentFolder: folderNames){
            File[] subFolderTxtFiles = finder(parentFolder.getPath());
            for(File file: subFolderTxtFiles){
                txtFiles.add(new TxtFile(parentFolder.getName(), file.getName(), file));
            }
        }
        for(TxtFile txtFile: txtFiles){
            txtFile.setListOfWordWithoutStopWords(removeStopWords(txtFile.getSplitContent(),stopWord));
        }
        displayMenu(data);

        long start = System.currentTimeMillis();
        for(TxtFile txtFile:txtFiles){
            DictionaryInterface<String,Integer> tempHash = new HashedDictionary<>();
            tempHash.setCheckCollision(data.isCheckCollision());
            tempHash.setCheckHashFunction(data.isCheckHashFunction());
            fileName = txtFile.getFileName() + "_" + txtFile.getParentFolderName();
            for(String word: txtFile.getListOfWordWithoutStopWords()){
                if(tempHash.getValue(word) == null){
                    tempHash.put(word,1);
                }
                else{
                    tempHash.put(word, tempHash.getValue(word)+1);
                }
            }
            insertTempHashToData(tempHash,data,fileName);
        }

        long end = System.currentTimeMillis();
        float indexingTime= ((end - start) / 1000);
        int collusionCount = data.getCollisionCount();
        float max=searchTimeFromTxt(data);
        Scanner sc = new Scanner(System.in);
        System.out.print(">Search: ");
        String word = sc.nextLine();
        search(data,word);
    }
}
