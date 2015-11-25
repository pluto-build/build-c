package build.pluto.buildc.compiler;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sugarj.common.Exec;
import org.sugarj.common.FileCommands;
import org.sugarj.common.StringCommands;
import org.sugarj.common.Exec.ExecutionError;
import org.sugarj.common.Exec.ExecutionResult;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.util.Pair;

import build.pluto.buildc.compiler.CCompiler.CCompilerResult;

public class GCLinker implements CLinker {
	public static final GCLinker instance = new GCLinker(); 
	private GCLinker() { }
	@Override
	public CLinkerResult link(File targetDir) throws Exception {
		List<String> cmd = new ArrayList<>();
		cmd = new ArrayList<>();
		cmd.add("gcc");
		cmd.add("-v");
		cmd.add("-H");
		cmd.add("-Wall");
		cmd.add("-g");
		cmd.add("-o");
		
		String errOut = null;
		boolean ok = false;
		try {
			ExecutionResult linkResult = null;
			File[] objFiles = targetDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(".o");
				}
			});
			
			cmd.add(FileCommands.dropExtension(objFiles[0].getName()));
			for (int i = 0; i < objFiles.length; i++) {
				cmd.add(FileCommands.toWindowsPath(objFiles[i].getAbsolutePath()));
			}
		
			// TODO Handle Linking error output:
			linkResult = Exec.run(targetDir, cmd.toArray(new String[cmd.size()]));
			ok = true;
			errOut = StringCommands.printListSeparated(linkResult.errMsgs, "\n");

		} catch (ExecutionError e) {
			errOut = StringCommands.printListSeparated(e.errMsgs, "\n");
		}

		if (!ok) {
			List<Pair<SourceLocation, String>> errors = CompilerUtil.parseGCCErrors(errOut);
			if (!errors.isEmpty())
				throw new SourceCodeException(errors);
		}
		File generatedFile =  CompilerUtil.extractGeneratedFile(targetDir);
		List<File> requiredFiles = CompilerUtil.extractRequiredFiles(errOut);

		return new CLinkerResult(generatedFile, requiredFiles);
	}

}
