package util.listdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class does not extend the typical FileProcessor interface because it is NOT threadsafe. It relies on the files to be processed in order. 
 * However, it could be thought of a FileProcessor from DataCollection<E> to DataCollection<E>
 * @author jrc436
 *
 */
public class ReorderListProcessor {
	private static List<FileParts> rebase(File inputDirectory, File outputDirectory, String rebase) {
		List<FileParts> fp = captureAll(inputDirectory);
		Collections.sort(fp, FileParts.getFileNumComparator());
		fp.get(0).readFile();
		System.err.println(fp.get(0).toString());
		outputDirectory.mkdirs();
		addRepaths(outputDirectory, fp, rebase);
		return fp;
	}
	public static void fixDataCollection(File inputDirectory, File outputDirectory, String rebase) {
		List<FileParts> fp = rebase(inputDirectory, outputDirectory, rebase);
		String lastKeyLine = "";
		for (String s : fp.get(0).lines) { 
			if (DataCollection.isKeyLine(s)) {
				lastKeyLine = s;
			}
		}
		String lastNewKeyLine = lastKeyLine;
		String fileResponsible = fp.get(0).toString();
		for (FileParts f : fp) {
			System.err.println("Fixing:" + f.toString()+", using: "+fileResponsible);
			f.readFile();
			lastKeyLine = altFix(f.lines, lastKeyLine);
			if (!lastNewKeyLine.equals(lastKeyLine)) {
				fileResponsible = f.toString();
			}
			lastNewKeyLine = lastKeyLine;
			f.writeFile();
			f.clean();
		}
	}
	public static void fixFileNames(File inputDirectory, File outputDirectory, String rebase) {
		List<FileParts> fp = rebase(inputDirectory, outputDirectory, rebase);
		for (FileParts f : fp) {
			f.readFile();
			f.writeFile();
			f.clean();
		}
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Specify inputDirectory first, and whether the files also need their headers fixed / is a broken DataCollection (true or false)");
			System.err.println("Optionally, the third argument would be a non-default output directory, and the fourth would be a new basename");
			System.exit(1);
		}
		File inputDirectory = new File(args[0]);
		File outputDirectory = args.length > 2 ?  new File(args[2]) : inputDirectory.getParentFile().toPath().resolve(inputDirectory.getName()+"-f").toFile();
		String rebase = args.length > 2 ? args[2] : null;
		try {
			fixDataCollection(inputDirectory, outputDirectory, rebase);
		}
		catch (IllegalArgumentException ie) {
			ie.printStackTrace();
			System.err.println("Not a DataCollection. Proceeding with a renumbering");
			fixFileNames(inputDirectory, outputDirectory, rebase);
		}
	}
	private static String altFix(List<String> second, String lastKeyLine) {
		if (!DataCollection.isKeyLine(lastKeyLine)) {
			throw new IllegalArgumentException("The 'first' list has still not been fixed, so this call is invalid");
		}
		String lastKeyLine2 = lastKeyLine;
		for (String s : second) {
			if (DataCollection.isKeyLine(s)) {
				lastKeyLine2 = s;
			}
		}
		if (!DataCollection.isKeyLine(second.get(0))) {
			second.add(0, lastKeyLine);
		}
		return lastKeyLine2;
	}
	public static List<FileParts> captureAll(File inputDirectory) {
		List<FileParts> fileparts = new ArrayList<FileParts>();
		for (File f : inputDirectory.listFiles()) {
			fileparts.add(capture(f));
		}
		return fileparts;
	}
	private static Path getNewFilePath(File outputDir, FileParts fp, String rebase, int totalFiles) {
		if (rebase != null) {
			return outputDir.toPath().resolve(fp.rebaseFormat(rebase, totalFiles));
		}
		return outputDir.toPath().resolve(fp.format(totalFiles));
	}
	public static void addRepaths(File outputDir, List<FileParts> fp, String rebase) {
		int totalFiles = fp.size();
		for (FileParts f : fp) {
			f.repath = getNewFilePath(outputDir, f, rebase, totalFiles);
		}
	}
	public static void copyAll(List<FileParts> fp) throws IOException {
		for (FileParts f : fp)  {
			Files.copy(f.origFile.toPath(), f.repath);
		}
	}
	
	/**
	 * The naming conventions of files require there to be a basename-##.ext .
	 */
	private static FileParts capture(File f) {
		String fileName = f.getName();
		String[] dashParts = fileName.split("-");
		String numAndExt = "";
		String baseName = "";
		if (dashParts.length >= 2) {
			numAndExt = dashParts[dashParts.length-1];
			for (int i = 0; i < dashParts.length-1; i++) {
				baseName += dashParts[i] + "-";
			}
			baseName = baseName.substring(0, baseName.length()-1);
		}
		else {
			System.err.println("Warning: no dashes found in file. Parsing could be incorrect (parsed from : "+fileName+")");
			if (!fileName.contains(".")) {
				return new FileParts(fileName, "", -1, f);
			}
			String[] parts = fileName.split("\\.");
			baseName = parts[0];
			for (int i = 1; i < parts.length; i++) {
				numAndExt += parts[i];
			}
		}
		String[] extParse = numAndExt.split("\\.");
		if (extParse.length > 2) {
			System.err.println("Warning: multiple periods in file extension. Parsing could be incorrect (parsed from: "+numAndExt+")");
		}
		else if (extParse.length < 2) {
			System.err.println("Warning: no file extension found. Parsing could be incorrect (parsed from: "+numAndExt+")");
			if (numAndExt.matches("\\d+")) {
				return new FileParts(baseName, "", Integer.parseInt(numAndExt), f);
			}
			System.err.println("Warning: no number found. Parsing could be incorrect (parsed from: "+numAndExt+")");
			return new FileParts(baseName, "", -1, f);
		}
		int num = extParse[0].matches("\\d+") ? Integer.parseInt(extParse[0]) : -1;
		int s = num == -1 ? 0 : 1;
		String ext = "";
		for (int i = s; i < extParse.length; i++) {
			ext += extParse[i] + ".";
		}
		ext = ext.substring(0, ext.length()-1);
		return new FileParts(baseName, ext, num, f);
	}
	static class FileParts {
		private final String baseName;
		private int fileNum;
		private final String extension;
		private final File origFile;
		private Path repath;
		private List<String> lines;
		public static Comparator<FileParts> getFileNumComparator() {
			return new Comparator<FileParts>() {
				@Override
				public int compare(FileParts o1, FileParts o2) {
					if (o1.fileNum == o2.fileNum) {
						return 0;
					}
					else if (o1.fileNum > o2.fileNum) {
						return 1;
					}
					return -1;
				}
			};
		}
		public FileParts(String baseName, String extension, int fileNum, File origFile) {
			this.baseName = baseName;
			this.extension = extension;
			this.fileNum = fileNum;
			this.origFile = origFile;
		}
		private String extString() {
			return extension == null || extension.isEmpty() ? "" : "."+extension;
		}
		public String toString() {
			if (fileNum == -1) {
				return baseName + extString();
			}
			return baseName + "-" + fileNum  + extString();
		}
		
		public String format(int totalNumberOfFiles) {
			if (fileNum == -1) {
				if (totalNumberOfFiles != 1) {
					System.err.println("Warning: naming scheme is assigning a non-numbered file, despite having multiple files");
				}
				return baseName + extString();
			}
			int digitsNeeded = 1 + ((int)Math.floor(Math.log10(totalNumberOfFiles)));
			String formatNumber = String.format("%0"+digitsNeeded+"d", fileNum);
			return baseName + "-" + formatNumber + extString();
		}
		public String rebaseFormat(String newBase, int totalNumberOfFiles) {
			return new FileParts(newBase, extension, fileNum, origFile).format(totalNumberOfFiles);
		}
		public void readFile() {
			try {
				this.lines = Files.readAllLines(origFile.toPath());
			}
			catch (IOException ie) {
				System.err.println("Unable to read file: "+origFile);
				ie.printStackTrace();
				System.exit(1);
			}
		}
		public void clean() {
			this.lines = null;
		}
		public void writeFile() {
			try {
				FileWriter fw = new FileWriter(repath.toFile());
				for (String s : lines) {
					fw.write(s + System.getProperty("line.separator"));
				}
				fw.close();
			} catch (IOException e) {
				System.err.println("Unable to write to file: "+repath);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
