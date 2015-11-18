/**
 * 
 */
package build.pluto.buildc.compiler;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sugarj.common.Exec;
import org.sugarj.common.Exec.ExecutionError;
import org.sugarj.common.Exec.ExecutionResult;
import org.sugarj.common.FileCommands;
import org.sugarj.common.StringCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.util.Pair;

/**
 * @author talen
 *
 */
public class GCCompiler implements CCompiler {
	
	private static final long serialVersionUID = -8338564127815270446L;

	private final static String ERR_PAT = ": error: ";
	private final static String DEP_PAT = "checksum: ";
	public static final GCCompiler instance = new GCCompiler(); 
	private GCCompiler() { }
	@Override
	public CCompilerResult compile(Collection<File> sourceFiles, File targetDir)
			throws Exception {

		List<String> cmd = new ArrayList<>();

		cmd.add("gcc");
		cmd.add("-c");
		cmd.add("-v");
		cmd.add("-H");
		for (File sourceFile : sourceFiles){
			cmd.add(FileCommands.toWindowsPath(sourceFile.getAbsolutePath()));
		}
		
		String errOut = null;
		boolean ok = false;
		try {
			FileCommands.createDir(targetDir);
			ExecutionResult compileResult = Exec.run(targetDir,cmd.toArray(new String[cmd.size()]));
			ExecutionResult linkResult = null ;
			ok = true;
			if (ok) {
				cmd = new ArrayList<>();
				cmd.add("gcc");
				cmd.add("-v");
				cmd.add("-H");
				cmd.add("-Wall");
				cmd.add("-g");
				cmd.add("-o");
				cmd.add("Application");
				
				File[] objFiles = targetDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getAbsolutePath().endsWith(".o");
					}
				});
				
				for (int i = 0; i < objFiles.length; i++) {
					cmd.add(FileCommands.toWindowsPath(objFiles[i].getAbsolutePath()));
				}
				 ok = false;
				
				//TODO Handle Linking error output: 
				 linkResult = Exec.run(targetDir,cmd.toArray(new String[cmd.size()]));
				 ok = true;
			     errOut = StringCommands.printListSeparated(compileResult.errMsgs, "\n");
			}
			
			
		} catch (ExecutionError e) {
			errOut = StringCommands.printListSeparated(e.errMsgs, "\n");
		}

		if (!ok) {
			List<Pair<SourceLocation, String>> errors = parseGCCErrors(errOut);
			if (!errors.isEmpty())
				throw new SourceCodeException(errors);
		}
		Map<File, List<File>> generatedFiles =  extractGeneratedFiles(sourceFiles,targetDir);
		List<File> requiredFiles = extractRequiredFiles(errOut);

		return new CCompilerResult(generatedFiles, requiredFiles);
	}
	
	private static List<File> extractRequiredFiles(String errOut) {
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
	//Extract generated files for C
	private static Map<File, List<File>> extractGeneratedFiles(Collection<File> sourceFiles,File targetDir) {
		
		Map<File, List<File>> generatedFiles = new HashMap<>();
		Map<String,File> objectFiles =  FileCommands.filesMap(targetDir,new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith(".o");
			}
		});
		
		for (File file : sourceFiles) {
			ArrayList<File> files = new ArrayList<>();
			String nameKey = FileCommands.dropExtension(file.getName());
			File objectFile = objectFiles.get(nameKey);
			files.add(objectFile);
			generatedFiles.put(file, files);
			
		}
		return generatedFiles;
	}
	
	private static List<Pair<SourceLocation, String>> parseGCCErrors(String s) {
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
	
	
}
