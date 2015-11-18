package build.pluto.buildc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.util.Pair;

import build.pluto.buildc.compiler.CCompiler;
import build.pluto.buildc.compiler.CCompiler.CCompilerResult;
import build.pluto.builder.BuildRequest;
import build.pluto.builder.Builder;
import build.pluto.builder.BuilderFactory;
import build.pluto.builder.BuilderFactoryFactory;
import build.pluto.output.None;

public class CBuilder extends Builder<CInput,None> {
	
	public static BuilderFactory<CInput, None, CBuilder> factory = BuilderFactoryFactory.of(CBuilder.class, CInput.class);
	public CBuilder(CInput input) {
		super(input);
		
	}
	
	@Override
	protected None build(CInput input) throws Throwable {

		List<File> inputFiles = new ArrayList<>();
		File targetDir = input.getTargetDir();
		CCompiler compiler = input.getCompiler();
		
		for (File p : input.getInputFiles())
			if (!inputFiles.contains(p)) {
				inputFiles.add(p);
			}
		for (File p : inputFiles)
			require(p);
		
		FileCommands.createDir(targetDir);
		CCompilerResult compilerResult;
		try {
			compilerResult = compiler.compile(inputFiles, targetDir);
		} catch (SourceCodeException e) {
			StringBuilder errMsg = new StringBuilder("The following errors occured during compilation:\n");
			for (Pair<SourceLocation, String> error : e.getErrors()) {
				errMsg.append(FileCommands.dropDirectory(error.a.file) + "(" + error.a.lineStart + ":" + error.a.columnStart + "): " + error.b);
			}
			throw new IOException(errMsg.toString(), e);
		}
		
		for (Collection<File> gens : compilerResult.getGeneratedFiles().values())
			for (File gen : gens){
				provide(gen);
			}
		//TODO GCC does not need to compile required header files what we should do here 
		for (File p : compilerResult.getLoadedFiles()) {
			System.out.println("Required : "+p.getAbsolutePath());
			switch (FileCommands.getExtension(p)) {
			case "h":
				break;
			}
			require(p);
		}
		return None.val;
	}
	@Override
	protected String description(CInput input) {
		StringBuilder builder = new StringBuilder();
		for (File f : input.getInputFiles())
				builder.append(f.getName()).append(", ");
		String list = builder.toString();
		if (!list.isEmpty())
			list = list.substring(0, list.length() - 2);

		return "Compile C files " + list + "\n";
	}


	@Override
	public File persistentPath(CInput input) {
		return new File(input.getTargetDir(), "compile.c."+ input.getInputFiles().hashCode() +".dep");
		
	}
	public static BuildRequest<CInput, None, CBuilder, BuilderFactory<CInput, None, CBuilder>> request(CInput input) {
		return new BuildRequest<>(factory,input);
	}
	
}
