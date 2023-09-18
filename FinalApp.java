import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.Writer;

/**
 * FinalApp.java
 * 174282 Java Programming IV : Advanced Java Programming Structures - University of California, San Diego
 * Professor Norman McEntire
 *
 *****************************************
 * Selecting the following 3 Java Skills *
 * Demo iostreams                        *
 * Demo Regular Expressions              *
 * Demo Streams                          *
 *****************************************
 *
 * September 9, 2023
 * Java application named FinalApp which will iterate through a user selected root folder and traverse all subfolders 
 * to find all instances of AutoCAD .DWG files. Using REGEX we will then be able to look into these .DWG files and
 * discover what version they were saved as. 
 * 
 * DWG versions since the inception of the filetype in 1982;
 * MC0.0 - DWG Version 1.1
 * AC1.2 - DWG Version 1.2
 * AC1.4 - DWG Version 1.4
 * AC1.50 - DWG Version 2.0
 * AC2.10 - DWG Version 2.10
 * AC1002 - DWG Version 2.5
 * AC1003 - DWG Version 2.6
 * AC1004 - DWG Version 9
 * AC1006 - DWG Version 10
 * AC1009 - DWG Version 11/12 (LT R1/R2)
 * AC1012 - DWG Version 13 (LT95)
 * AC1014 - DWG Version 14, 14.01 (LT97/LT98)
 * AC1015 - DWG Version 2000/2000i/2002
 * AC1018 - DWG Version 2004/2005/2006
 * AC1021 - DWG Version 2007/2008/2009
 * AC1024 - DWG Version 2010/2011/2012
 * AC1027 - DWG Version 2013/2014/2015/2016/2017
 * AC1032 - DWG Version 2018/2019/2020/2021/2022/2023
 * 
 * The first 5 to 6 characters of the .DWG file would designate the file version. This has been human readable since the first version of .DWG.
 * You can view the version string by opening the .DWG file in any standard text file reader, such as Microsoft Notepad or Notepad++.
 * For example, an AC1009 Release 12 .DWG would have the following in the first line of the file:
 * AC1009        �  �  E    =  @       �-    �  )    �  �    �  �    �  �     
 * 
 * Note: DWG versions, more or less, correspond to AutoCAD versions as well. For example AC1009 DWG Version 11/12 corresponds with files native
 * to AutoCAD 11 or AutoCAD 12. AC1032 files could have been saved in any AutoCAD versions from AutoCAD 2018 through AutoCAD 2023.
 * 
 * This program utilizes prewritten .DWG files (Created using DraftSight) that were saved in multiple formats. The directory structure
 * looks like this;
 * 
 * ├───FolderA
 * │   ├───Leaf1A
 * │   │       DrawingR12.dwg
 * │   │
 * │   ├───Leaf1B
 * │   │       DrawingR14.dwg
 * │   │
 * │   └───Leaf1C
 * │           DrawingR2000.dwg
 * │
 * ├───FolderB
 * │   ├───Leaf2A
 * │   │       DrawingR2004.dwg
 * │   │
 * │   ├───Leaf2B
 * │   │       DrawingR2007.dwg
 * │   │
 * │   └───Leaf2C
 * │           DrawingR2010.dwg
 * │
 * └───FolderC
 *     ├───Leaf3A
 *     │       DrawingR2013.dwg
 *     │
 *     ├───Leaf3B
 *     │       DrawingR2018.dwg
 *     │
 *     └───Leaf3C
 *             DrawingR2010.dwg
 * 
 * @author James Falconer, MSc
 *
 */

public class FinalApp
{

	private static final String dwgVersionRegex = "^(MC0.0|AC1.2|AC1.4|AC1.50|AC2.10|AC1002|AC1003|AC1004|AC1006|AC1009|AC1012|AC1014|AC1015|AC1018|AC1021|AC1024|AC1027|AC1032)";
	private static final Pattern pattern = Pattern.compile(dwgVersionRegex);

	public static void main(String[] args)
	{
		//ARGUMENT Check if Argument is empty
		if (args.length == 0)
		{
			System.out.println("Run again with drive and foldername to start searching from eg. java FinalApp C:\\FinalApp");
		}
		else
		{
			String rootPath = args[0];	// Root directory from user
			StringBuilder stringBuilder = new StringBuilder(); // StringBuilder to hold matched values and file locations

			/**
			 * 
			 * Using Stream to store folders and filenames that were
			 * found using the Files.walk method. The stream will then
			 * allow us to use some functions to filter these results.
			 * First we will select just files by using isRegularFile.
			 * Second, we just want files that end with the .dwg extension.
			 * And finally, we send each path to the regexFile method including
			 * the stringBuilder String.
			 * 
			 */

			System.out.println("*****************************************************************************");
			System.out.println("* Using Stream to read all folder names and files from path selected by user.");
			System.out.println("*****************************************************************************\n");

			try (Stream<Path> paths = Files.walk(Paths.get(rootPath)))
			{
				paths.filter(Files::isRegularFile) 					// Filter to include only files, excluding directories
				.filter(path -> path.toString().toLowerCase().endsWith(".dwg"))	// Filter to then include only .DWG files (changes case if non-lowercase letters used)
				.forEach(path -> regexFile(path, stringBuilder));	// Sends each path to regexFile method(path and String)
				
				System.out.println("******************************************");
				System.out.println("* DONE reading all folder names and files!");
				System.out.println("******************************************\n");
				
				System.out.println("*****************************************************************************************************************");
				System.out.println("* Used REGEX method (regexFile) to find all files that have a matching signature in first 6 characters of a file.");
				System.out.println("*****************************************************************************************************************\n");

				System.out.println(stringBuilder.toString());
				
				/**
				 * 
				 * Using IOStream to save stringBuilder to a text file. Will save to
				 * root folder selected by user. The stringBuilder will contain
				 * the version of the file (based on signature), and the complete path
				 * to the file that matches the signature.  
				 * 
				 */				
				
				System.out.println("**********************************************************************************************");
				System.out.println("* Using IOStream to write all DWG files and their locations to a text file named DWG_List.txt.");
				System.out.println("**********************************************************************************************\n");
				
		        String filePath = (rootPath + "\\DWG_List.txt"); // Specify your desired file path

		        try (FileOutputStream fos = new FileOutputStream(filePath);
		        Writer writer = new OutputStreamWriter(fos))
		        {
		        	writer.write(stringBuilder.toString());
		        }
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("*********************************************************");
			System.out.println("* File " + rootPath + "\\DWG_List.txt Saved successfully.");
			System.out.println("*********************************************************\n");
		}
	}

	private static void regexFile(Path path, StringBuilder stringBuilder)
	{
		try
		{
			/**
			 * 
			 * Using Regular Expression (Defined as dwgVersionRegex shown at top). This regexFile method takes two values, 
			 * a Path and the stringBuilder. First, we create a byte array object to read the whole content of the file. 
			 * This will allow us to extract the first 6 characters from the file represented by the Path and check if 
			 * these characters match the regex pattern. If a match is found, the method appends the match information 
			 * to the stringBuilder. Method run for each file filtered by Stream.
			 * 
			 */
			
			byte[] bytes = Files.readAllBytes(path);
			String signature = new String(bytes, 0, Math.min(bytes.length, 6));  // Creates a string of the first 6 characters of the bytes object.
			if (pattern.matcher(signature).matches()) 
			{
				stringBuilder.append("DWG Version: ").append(signature)
				.append(" in file; ").append(path.toString()).append("\n");
			}
		}
		catch (IOException e)
		{
			System.err.println("Error reading file; " + path.toString());
			e.printStackTrace();
		}
	}
}