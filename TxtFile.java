import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Locale;

public class TxtFile {
    private String parentFolderName;
    private String fileName;
    private String[] splitContent;
    private LinkedList<String> listOfWordWithoutStopWords;

    private static String DELIMITERS = "[-+=" + " " + "\r\n " + "1234567890" + "’'\"" + "(){}<>\\[\\]" + ":" + "," + "‒–—―" + "…" + "!" +"." + "«»" + "-‐" + "?" + "‘’“”" + ";" +
            "/" + "⁄" + "␠" + "·" + "&" + "@" + "*" + "\\" + "•" + "^" + "¤¢$€£¥₩₪" + "†‡" + "°" + "¡" + "¿" + "¬" + "#" + "№" + "%‰‱" + "¶" + "′" + "§" + "~" + "¨" +
            "_" + "|¦" + "⁂" + "☞" + "∴" + "‽" + "※" + "]";

    public TxtFile(String parentFolderName, String fileName, File file) throws Exception {
        this.parentFolderName = parentFolderName;
        this.fileName = fileName;
        readContentOfFile(file);
    }

    private void readContentOfFile(File file) throws Exception{
        BufferedReader bfr= null;
        String line;
        StringBuilder content = new StringBuilder();
        try {
            bfr = new BufferedReader(new FileReader(file));
            while((line = bfr.readLine()) != null){
                content.append(line).append(" ");
            }
            splitContent = content.toString().toLowerCase(Locale.ENGLISH).split(DELIMITERS);
            bfr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getSplitContent() {
        return splitContent;
    }

    public void setSplitContent(String[] splitContent) {
        this.splitContent = splitContent;
    }

    public LinkedList<String> getListOfWordWithoutStopWords() {
        return listOfWordWithoutStopWords;
    }

    public void setListOfWordWithoutStopWords(LinkedList<String> listOfWordWithoutStopWords) {
        this.listOfWordWithoutStopWords = listOfWordWithoutStopWords;
    }
}
