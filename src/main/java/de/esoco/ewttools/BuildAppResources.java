//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'gewt' project.
// Copyright 2015 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewttools;

import de.esoco.ewt.app.EWTEntryPoint;

import de.esoco.lib.text.TextConvert;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gwt.i18n.tools.I18NSync;
import com.google.gwt.resources.css.InterfaceGenerator;


/********************************************************************
 * Creates the resources for a GEWT application by concatenating all string
 * properties and CSS files into single files and then generates the code files
 * by invoking the tools {@link I18NSync} and {@link CssInterfaceGenerator} on
 * them.
 *
 * @author eso
 */
public class BuildAppResources
{
	//~ Static fields/initializers ---------------------------------------------

	private static final int    MAX_PROPERTY_LINES = 2500;
	private static final String GENERATED_DIR	   = "res/generated/";
	private static final String GENERATED_PACKAGE  = ".res.generated.";
	private static final String WAR_RESOURCE_DIR   = "war/data/res/";

	private static final PatternFilter CSS_FILES_FILTER		 =
		new PatternFilter(".*\\.css");
	private static final PatternFilter PROPERTY_FILES_FILTER =
		new PatternFilter(".*\\.properties");

	private static final List<String> EXCLUDED_DIRS =
		Arrays.asList("generated", "img");

	private static Map<String, String> aProjectDirMap = new HashMap<>();

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 */
	private BuildAppResources()
	{
	}

	//~ Static methods ---------------------------------------------------------

	/***************************************
	 * Executes this application.
	 *
	 * @param rArgs The app arguments
	 */
	public static void main(String[] rArgs)
	{
		try
		{
			if (rArgs.length == 0)
			{
				printUsage();
			}

			String  sProjectDir   = null;
			boolean bHierarchical = false;
			int     nNextArg	  = 0;

			if ("-hierarchical".equals(rArgs[0]))
			{
				bHierarchical = true;
				nNextArg++;
			}
			else if ("-project".equals(rArgs[0]))
			{
				nNextArg++;
				sProjectDir = rArgs[nNextArg++];
			}

			Class<?> rAppClass = Class.forName(rArgs[nNextArg++]);

			if (sProjectDir != null)
			{
				aProjectDirMap.put(rAppClass.getSimpleName(), sProjectDir);
			}

			String		 sTargetDir = getBaseDir(rAppClass) + GENERATED_DIR;
			String		 sBaseName  = rAppClass.getSimpleName();
			List<String> aExtraDirs = new ArrayList<>();

			for (int i = nNextArg; i < rArgs.length; i++)
			{
				aExtraDirs.add(rArgs[i]);
			}

			String sPropertiesBase = sTargetDir + sBaseName + "Strings";
			String sCssBase		   = sTargetDir + sBaseName + "Css";

			Collection<String> aStringDirs =
				getResourceDirs(rAppClass, aExtraDirs, bHierarchical, true);
			Collection<String> aCssDirs    =
				getResourceDirs(rAppClass, aExtraDirs, bHierarchical, false);

			concatenatePropertyFiles(aStringDirs, sPropertiesBase);
			concatenateCssFiles(aCssDirs, sCssBase);
			generateStringClasses(rAppClass);
			copyStringsToWarData(rAppClass);

			System.out.printf("OK\n");
		}
		catch (Exception e)
		{
			System.out.println("ERR: " + e.getMessage());
			e.printStackTrace();
			printUsage();
		}
	}

	/***************************************
	 * Closes all elements of a collection.
	 *
	 * @param rCloseables A collection of {@link Closeable} elements
	 */
	private static void closeAll(Collection<? extends Closeable> rCloseables)
	{
		for (Closeable rCloseable : rCloseables)
		{
			try
			{
				rCloseable.close();
			}
			catch (IOException e)
			{
				System.err.printf("Error closing collection: %s\n", e);
				e.printStackTrace();
			}
		}
	}

	/***************************************
	 * Concatenates the CSS files of the target application into a single large
	 * file.
	 *
	 * @param  rDirectories    The resource directories from which to read the
	 *                         CSS files
	 * @param  sTargetBaseName The base name for the target files to write to
	 *
	 * @throws IOException If reading or writing a file fails
	 */
	private static void concatenateCssFiles(
		Collection<String> rDirectories,
		String			   sTargetBaseName) throws IOException
	{
		String  sTargetFile = sTargetBaseName + ".css";
		Writer  aCssWriter  = new BufferedWriter(new FileWriter(sTargetFile));
		boolean bFirstFile  = true;

		try
		{
			for (String sDirectory : rDirectories)
			{
				File aDirectory = new File(sDirectory);

				PatternFilter rFilter   = CSS_FILES_FILTER;
				String[]	  aCssFiles = aDirectory.list(rFilter);

				for (String sCssFile : aCssFiles)
				{
					if (bFirstFile)
					{
						System.out.printf("Writing %s\n", sTargetFile);
						bFirstFile = false;
					}

					String sHeader =
						String.format("\n/*--------------------------------------------------------------------\n" +
									  "  --- %s\n" +
									  "  --------------------------------------------------------------------*/\n\n",
									  sCssFile);

					aCssWriter.write(sHeader);
					writeFile(sDirectory + sCssFile, aCssWriter);
					System.out.printf(" + %s%s\n", sDirectory, sCssFile);
				}
			}
		}
		finally
		{
			aCssWriter.close();
		}
	}

	/***************************************
	 * Concatenates the string properties files containing the resource strings
	 * of the target application into a single files, separated by locale. If a
	 * certain size is exceeded the file will be split into multiple parts to
	 * prevent GWT compile errors.
	 *
	 * @param  rDirectories    The resource directories from which to read the
	 *                         property files
	 * @param  sTargetBaseName The base name for the target files to write to
	 *
	 * @throws IOException If reading or writing a file fails
	 */
	private static void concatenatePropertyFiles(
		Collection<String> rDirectories,
		String			   sTargetBaseName) throws IOException
	{
		Map<String, Writer> aWriters = new HashMap<>();

		try
		{
			Set<String> aProcessedFiles = new HashSet<>();
			Writer	    aDefaultWriter  = null;
			String	    sTarget		    = null;
			int		    nTotalLines     = -1;
			int		    nFileCount	    = 0;

			for (String sDirectory : rDirectories)
			{
				Collection<String> aDefaultFiles =
					getDefaultPropertyFiles(sDirectory);

				// first concatenate the default files
				for (String sInputFile : aDefaultFiles)
				{
					// don't process equal-named files twice
					if (!aProcessedFiles.contains(sInputFile))
					{
						aProcessedFiles.add(sInputFile);

						int nLines = countLines(sDirectory + sInputFile);

						if (nTotalLines < 0 ||
							nTotalLines + nLines > MAX_PROPERTY_LINES)
						{
							sTarget     = sTargetBaseName;
							nTotalLines = nLines;

							closeAll(aWriters.values());
							aWriters.clear();

							if (++nFileCount > 1)
							{
								sTarget += nFileCount;
							}

							aDefaultWriter =
								createPropertiesWriter(sTarget, "");
							aWriters.put("DEFAULT", aDefaultWriter);
							System.out.printf("Writing %s\n", sTarget);
						}
						else
						{
							nTotalLines += nLines;
						}

						writePropertiesHeader(sInputFile, aDefaultWriter);
						writeFile(sDirectory + sInputFile, aDefaultWriter);
						System.out.printf(" + %s%s\n", sDirectory, sInputFile);

						// then lookup the locale-specific files for the current
						// default file and concatenate them for each locale if
						// they exist
						int nDotPos = sInputFile.indexOf('.');

						String sLocalePattern =
							sInputFile.substring(0, nDotPos) + "_.*" +
							sInputFile.substring(nDotPos);

						String[] aLocaleFiles =
							new File(sDirectory).list(new PatternFilter(sLocalePattern));

						for (String sLocaleFile : aLocaleFiles)
						{
							int nLocaleIndex = sLocaleFile.indexOf('_');

							String sLocale =
								sLocaleFile.substring(nLocaleIndex + 1,
													  sLocaleFile.indexOf('.'));

							Writer aLocaleWriter = aWriters.get(sLocale);

							if (aLocaleWriter == null)
							{
								aLocaleWriter =
									createPropertiesWriter(sTarget, sLocale);

								aWriters.put(sLocale, aLocaleWriter);
							}

							writePropertiesHeader(sLocaleFile, aLocaleWriter);
							writeFile(sDirectory + sLocaleFile, aLocaleWriter);
							System.out.printf(" + %s%s\n",
											  sDirectory,
											  sLocaleFile);
						}
					}
				}
			}
		}
		finally
		{
			closeAll(aWriters.values());
		}
	}

	/***************************************
	 * Concatenates all string properties into a single file in the WAR data
	 * directory.
	 *
	 * @param  rAppClass The application class for which to copy
	 *
	 * @throws IOException If copying a file fails
	 */
	private static void copyStringsToWarData(Class<?> rAppClass)
		throws IOException
	{
		String sDirectory  = getBaseDir(rAppClass) + GENERATED_DIR;
		String sTargetFile =
			WAR_RESOURCE_DIR + rAppClass.getSimpleName() + "Strings.properties";

		try (Writer aWriter = new BufferedWriter(new FileWriter(sTargetFile)))
		{
			Collection<String> aSourceFiles =
				getDefaultPropertyFiles(sDirectory);

			for (String sSourceFile : aSourceFiles)
			{
				writeFile(sDirectory + sSourceFile, aWriter);

				// TODO: also write files with locale
			}
		}
	}

	/***************************************
	 * Counts the lines in a text file.
	 *
	 * @param  sFile The name of the file to count the lines of
	 *
	 * @return The number of lines in the file
	 *
	 * @throws IOException If reading from the file fails
	 */
	private static int countLines(String sFile) throws IOException
	{
		int nLines;

		try (LineNumberReader aReader =
			 new LineNumberReader(new FileReader(sFile)))
		{
			while (aReader.skip(Long.MAX_VALUE) > 0)
			{
			}

			nLines = aReader.getLineNumber() + 1;
		}

		return nLines;
	}

	/***************************************
	 * Creates a writer for the output of property files.
	 *
	 * @param  sBaseName The base name
	 * @param  sLocale   The locale (empty string for none)
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	private static BufferedWriter createPropertiesWriter(
		String sBaseName,
		String sLocale) throws IOException
	{
		StringBuilder aFileName = new StringBuilder(sBaseName);

		if (sLocale != null && sLocale.length() > 0)
		{
			aFileName.append('_').append(sLocale);
		}

		aFileName.append(".properties");

		return new BufferedWriter(new FileWriter(aFileName.toString()));
	}

	/***************************************
	 * Generates the application CSS classes by invoking {@link
	 * InterfaceGenerator}.
	 *
	 * @param  rAppClass The application class
	 *
	 * @throws IOException If accessing files fails
	 */
	@SuppressWarnings("unused")
	private static void generateCssClasses(Class<?> rAppClass)
		throws IOException
	{
		String sPackage   = rAppClass.getPackage().getName();
		String sDirectory = getBaseDir(rAppClass);

		String[] aCssFiles = new File(sDirectory).list(CSS_FILES_FILTER);

		for (String sFile : aCssFiles)
		{
			String sTarget = sFile.substring(0, sFile.indexOf('.'));

			sTarget = TextConvert.capitalizedIdentifier(sTarget);

			StringBuilder aTargetName = new StringBuilder(sPackage);

			aTargetName.append('.');
			aTargetName.append(sTarget);

			String[] aArgs =
				new String[]
				{
					"-standalone", "-css", sDirectory + sFile, "-typeName",
					aTargetName.toString()
				};

			System.out.printf("Generating %s.java from %s\n",
							  aTargetName,
							  sFile);

			ByteArrayOutputStream aGeneratedData = new ByteArrayOutputStream();

			PrintStream aCaptureOut  = new PrintStream(aGeneratedData);
			PrintStream rStandardOut = System.out;

			System.setOut(aCaptureOut);
			CssInterfaceGenerator.main(aArgs);
			System.setOut(rStandardOut);
			aCaptureOut.flush();

			sTarget = sDirectory + sTarget + ".java";

			try (OutputStream aCssFile =
				 new BufferedOutputStream(new FileOutputStream(sTarget)))
			{
				aGeneratedData.writeTo(aCssFile);
			}
		}
	}

	/***************************************
	 * Generates the application strings classes by invoking {@link I18NSync}.
	 *
	 * @param  rAppClass The application class
	 *
	 * @throws IOException If accessing files fails
	 */
	private static void generateStringClasses(Class<?> rAppClass)
		throws IOException
	{
		String sPackage = rAppClass.getPackage().getName() + GENERATED_PACKAGE;

		Collection<String> aPropertyFiles =
			getDefaultPropertyFiles(getBaseDir(rAppClass) + GENERATED_DIR);

		for (String sFile : aPropertyFiles)
		{
			String sTargetClass =
				sPackage + sFile.substring(0, sFile.indexOf('.'));

			String[] aArgs =
				new String[]
				{
					sTargetClass, "-out", "src", "-createConstantsWithLookup"
				};

			System.out.printf("Generating %s.java from %s\n",
							  sTargetClass,
							  sFile);
			I18NSync.main(aArgs);
		}
	}

	/***************************************
	 * Generates the name of the base directory of a GEWT application. Always
	 * ends with a directory separator.
	 *
	 * @param  rAppClass The application class
	 *
	 * @return The base directory of the app
	 */
	private static String getBaseDir(Class<?> rAppClass)
	{
		String		  sPackage = rAppClass.getPackage().getName();
		String		  sRootDir = rAppClass.getSimpleName();
		StringBuilder aBaseDir = new StringBuilder();
		char		  cDirSep  = File.separatorChar;

		if (aProjectDirMap.containsKey(sRootDir))
		{
			sRootDir = aProjectDirMap.get(sRootDir);
		}

		// ../<app-name>/src/<package-path>/
		aBaseDir.append("..").append(cDirSep);
		aBaseDir.append(sRootDir).append(cDirSep);
		aBaseDir.append("src").append(cDirSep);
		aBaseDir.append(sPackage.replace('.', cDirSep)).append(cDirSep);

		return aBaseDir.toString();
	}

	/***************************************
	 * Returns a collection of the property files for the default locale (i.e.
	 * without a locale suffix) in a certain directory.
	 *
	 * @param  sDirectory The name of the directory to read the files from
	 *
	 * @return A new collection of property files
	 */
	private static Collection<String> getDefaultPropertyFiles(String sDirectory)
	{
		File		 aDirectory = new File(sDirectory);
		List<String> aFiles     = new ArrayList<>();

		for (String sFile : aDirectory.list(PROPERTY_FILES_FILTER))
		{
			if (sFile.indexOf('_') == -1)
			{
				aFiles.add(sFile);
			}
		}

		return aFiles;
	}

	/***************************************
	 * Returns the application resource directories to be searched for resource
	 * files.
	 *
	 * @param  rAppClass     The class hierarchy
	 * @param  rExtraDirs    Optional extra directories to add to the returned
	 *                       collection
	 * @param  bHierarchical
	 * @param  bIncludeRoots TRUE to include the root resource directories,
	 *                       FALSE to only include the sub-directories
	 *
	 * @return The class hierarchy
	 */
	private static Collection<String> getResourceDirs(
		Class<?>	 rAppClass,
		List<String> rExtraDirs,
		boolean		 bHierarchical,
		boolean		 bIncludeRoots)
	{
		Deque<String> aDirectories = new ArrayDeque<>();

		do
		{
			String sResourceDir =
				getBaseDir(rAppClass) + "res" + File.separatorChar;

			File aDirectory = new File(sResourceDir);

			if (aDirectory.exists())
			{
				if (bIncludeRoots)
				{
					aDirectories.push(sResourceDir);
				}

				for (File rFile : aDirectory.listFiles())
				{
					String sFilename = rFile.getName();

					if (rFile.isDirectory() &&
						!EXCLUDED_DIRS.contains(sFilename))
					{
						aDirectories.push(rFile.getPath() + File.separatorChar);
					}
				}
			}
		}
		while (bHierarchical &&
			   (rAppClass = rAppClass.getSuperclass()) != EWTEntryPoint.class);

		if (rExtraDirs != null)
		{
			for (String sDir : rExtraDirs)
			{
				File aDirectory = new File(sDir);

				if (aDirectory.exists())
				{
					aDirectories.add(sDir);

					for (File rFile : aDirectory.listFiles())
					{
						if (rFile.isDirectory())
						{
							aDirectories.add(rFile.getPath() +
											 File.separatorChar);
						}
					}
				}
			}
		}

		return aDirectories;
	}

	/***************************************
	 * Prints usage information to the console.
	 */
	private static void printUsage()
	{
		System.out.printf("USAGE: %s [-project <base dir> | -hierarchical] <app classname> <extra dirs>...\n",
						  BuildAppResources.class.getSimpleName());
		System.exit(1);
	}

	/***************************************
	 * Reads a certain file and writes it to the given output stream.
	 *
	 * @param  sFile   The name of the file to write
	 * @param  rWriter The target output stream
	 *
	 * @throws IOException If creating the input stream or transferring data
	 *                     fails
	 */
	private static void writeFile(String sFile, Writer rWriter)
		throws IOException
	{
		try (BufferedReader aIn = new BufferedReader(new FileReader(sFile)))
		{
			String sLine;

			while ((sLine = aIn.readLine()) != null)
			{
				rWriter.write(sLine);
				rWriter.write("\n");
				rWriter.flush();
			}
		}
	}

	/***************************************
	 * Writes a separating header into a target stream.
	 *
	 * @param  sText   The header Text
	 * @param  rWriter The output stream
	 *
	 * @throws IOException If writing data fails
	 */
	private static void writePropertiesHeader(String sText, Writer rWriter)
		throws IOException
	{
		String sHeader =
			String.format("\n#--------------------------------------------------------------------\n" +
						  "#--- %s\n" +
						  "#--------------------------------------------------------------------\n\n",
						  sText);

		rWriter.write(sHeader);
	}

	//~ Inner Classes ----------------------------------------------------------

	/********************************************************************
	 * A filename filter based on regular expressions.
	 *
	 * @author eso
	 */
	static class PatternFilter implements FilenameFilter
	{
		//~ Instance fields ----------------------------------------------------

		private Pattern aPattern;

		//~ Constructors -------------------------------------------------------

		/***************************************
		 * Creates a new instance.
		 *
		 * @param sRegex The regular expression pattern
		 */
		public PatternFilter(String sRegex)
		{
			aPattern = Pattern.compile(sRegex);
		}

		//~ Methods ------------------------------------------------------------

		/***************************************
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(File rDir, String sName)
		{
			return aPattern.matcher(sName).matches();
		}
	}
}
