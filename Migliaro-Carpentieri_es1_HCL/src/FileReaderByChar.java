import java.io.*;

public class FileReaderByChar
{
    final static int STRING_LENGTH_LIMIT = 256;

    private RandomAccessFile rf;
    private long prevCharFilePointer;

    public FileReaderByChar(String fileName) throws FileNotFoundException
    {
        rf = new RandomAccessFile(fileName,"r");
    }

    public char getNextChar() throws EOFException, IOException
    {
        prevCharFilePointer = rf.getFilePointer();
        if(rf.getFilePointer() < rf.length())
            return (char) rf.read();
        else
            throw new EOFException();
    }

    public void retract(){
        try {
            rf.seek((rf.getFilePointer()-1L));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
