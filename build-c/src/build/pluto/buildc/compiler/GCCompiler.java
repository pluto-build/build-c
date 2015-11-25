/**
 * 
 */
package build.pluto.buildc.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sugarj.common.Exec;
import org.sugarj.common.Exec.ExecutionError;
import org.sugarj.common.Exec.ExecutionResult;
import org.sugarj.common.FileCommands;
import org.sugarj.common.StringCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.util.Pair;

/**
 * @author talen
 *
 */
public class GCCompiler implements CCompiler {
	
	private static final long serialVersionUID = -8338564127815270446L;
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
			System.err.println(cmd.toString());
			ExecutionResult compileResult = Exec.run(targetDir,cmd.toArray(new String[cmd.size()]));
			
			ok = true;
			errOut = StringCommands.printListSeparated(compileResult.errMsgs, "\n");
			
			
		} catch (ExecutionError e) {
			errOut = StringCommands.printListSeparated(e.errMsgs, "\n");
		}

		if (!ok) {
			List<Pair<SourceLocation, String>> errors = CompilerUtil.parseGCCErrors(errOut);
			if (!errors.isEmpty())
				throw new SourceCodeException(errors);
		}
		Map<File, List<File>> generatedFiles =  CompilerUtil.extractGeneratedFiles(sourceFiles,targetDir);
		List<File> requiredFiles = CompilerUtil.extractRequiredFiles(errOut);

		return new CCompilerResult(generatedFiles, requiredFiles);
	}
	
	
	
	
	
}
