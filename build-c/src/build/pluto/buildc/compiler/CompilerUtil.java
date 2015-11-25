package build.pluto.buildc.compiler;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.util.Pair;

public class CompilerUtil {

	private final static String ERR_PAT = ": error: ";
	private final static String DEP_PAT = "checksum: ";
	//Extract generated files for C
		public static Map<File, List<File>> extractGeneratedFiles(Collection<File> sourceFiles,File targetDir) {
			
			Map<File, List<File>> generatedFiles = new HashMap<>();
			Map<String,File> objectFiles = new HashMap<>();
		    List<Path> filesList =  FileCommands.listFilesRecursive(targetDir.toPath(),new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(".o");
				}
			});
			for (Path path : filesList) {
				objectFiles.put(FileCommands.dropExtension(path.getFileName()).toString(),path.toFile());
			}

			for (File file : sourceFiles) {
				ArrayList<File> files = new ArrayList<>();
				files.add(objectFiles.get(FileCommands.dropExtension(file.getName())));
				generatedFiles.put(file, files);
				
			}
			return generatedFiles;
		}
   
		public static File extractGeneratedFile(File targetDir) {
	
			List<Path> filesList =  FileCommands.listFilesRecursive(targetDir.toPath(),new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(".exe");
				}
			});

			return filesList.get(0).toFile();
		}
		public static List<Pair<SourceLocation, String>> parseGCCErrors(String s) {
			List<Pair<SourceLocation, String>> errors = new LinkedList<>();
			int index = 0;
			System.err.println(s);

			while ((index = s.indexOf(ERR_PAT, index)) >= 0) {
				int lineStart = index - 1;
				while (s.charAt(lineStart) != ':') {
					lineStart--;
				}
				index = lineStart;
				lineStart--;

				while (s.charAt(lineStart) != ':') {
					lineStart--;
				}

				int line = Integer.parseInt(s.substring(lineStart + 1, index));
				int fileStart = lineStart - 1;
				while (s.charAt(fileStart) != '\n')
					fileStart--;
				String file = s.substring(fileStart + 1, lineStart);
				System.out.println("file : " + file);
				index += ERR_PAT.length();
				int errorEnd = s.indexOf("\n", index);
				System.out.println("index : " + index + " errorEnd : " + errorEnd);
				String msg = s.substring(index, errorEnd);
				System.err.println(msg);
				int columnLineStart = s.indexOf("\n", s.indexOf("\n", errorEnd));
				int columnSignIndex = s.indexOf("^", columnLineStart);
				int colStart = columnSignIndex - columnLineStart;
				int end = s.indexOf("\n", columnSignIndex);
				System.out.println(line + " colStart " + colStart);
				errors.add(Pair.create(new SourceLocation(new AbsolutePath(file), line, line, colStart, colStart), msg));
				index = end > 0 ? ++end : index;

						}
			return errors;
		}
		
		public static List<File> extractRequiredFiles(String errOut) {
			List<File> requiredFiles = new LinkedList<>();
			int index = 0;
				
			while ((index = errOut.indexOf(DEP_PAT, index)) >= 0) {
				index += DEP_PAT.length();
				
				while (errOut.charAt(index) != '\n'){
					index++;
				}
				index++;
				while (errOut.charAt(index) == '.') {
					
					while (errOut.charAt(index) == '.') {
						index++;
					}
					int to = errOut.indexOf('\n', index);
					String generatedPath = errOut.substring(index, to);
					index = to;
					index++;
					if (generatedPath.contains(".h")) {
						generatedPath = generatedPath.trim();
					}
					File file = new File(generatedPath);
					if (!requiredFiles.contains(file))
						requiredFiles.add(file);
				}
				
			}
			return requiredFiles;
		}
}
