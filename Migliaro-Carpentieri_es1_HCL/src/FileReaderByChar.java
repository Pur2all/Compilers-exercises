import java.io.*;

public class FileReaderByChar
{
	private final RandomAccessFile randomAccessFile;

	public FileReaderByChar(String filePath) throws FileNotFoundException
	{
		randomAccessFile = new RandomAccessFile(filePath, "r");
	}

	public char getNextChar() throws IOException
	{
		return (char) randomAccessFile.read();
	}

	public void retract()
	{
		try
		{
			randomAccessFile.seek(randomAccessFile.getFilePointer() - 1L);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
