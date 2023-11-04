public class FrequencyOfWords {
    private int frequency;
    private String fileName;

    public FrequencyOfWords(String fileName, int frequency) {
        this.frequency = frequency;
        this.fileName=fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}
