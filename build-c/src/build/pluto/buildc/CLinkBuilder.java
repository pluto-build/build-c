package build.pluto.buildc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.util.Pair;

import build.pluto.buildc.compiler.CLinker.CLinkerResult;
import build.pluto.buildc.compiler.GCCompiler;
import build.pluto.buildc.compiler.GCLinker;
import build.pluto.builder.BuildRequest;
import build.pluto.builder.Builder;
import build.pluto.builder.BuilderFactory;
import build.pluto.builder.BuilderFactoryFactory;
import build.pluto.output.None;
import build.pluto.stamp.LastModifiedStamper;
import build.pluto.stamp.Stamper;

public class CLinkBuilder extends Builder<CInput,None> {
	public static BuilderFactory<CInput, None, CLinkBuilder> factory = BuilderFactoryFactory.of(CLinkBuilder.class, CInput.class);
	public CLinkBuilder(CInput input) {
		super(input);
	}
	
	
	@Override
	protected None build(CInput input) throws Throwable {
		File targetDir = input.getTargetDir();
		GCLinker linker = input.getLinker();
		requireBuild(CBuilder.request(new CInput(input.getInputFiles(), targetDir, GCCompiler.instance)));
		CLinkerResult linkerResult;

		try {
			linkerResult = linker.link(targetDir);
		} catch (SourceCodeException e) {
			StringBuilder errMsg = new StringBuilder("The following errors occured during compilation:\n");
			for (Pair<SourceLocation, String> error : e.getErrors()) {
				errMsg.append(FileCommands.dropDirectory(error.a.file) + "(" + error.a.lineStart + ":"
						+ error.a.columnStart + "): " + error.b);
			}
			throw new IOException(errMsg.toString(), e);
		}

		File gen = linkerResult.getGeneratedFile();
		provide(gen);

		return None.val;

	}
	@Override
	protected String description(CInput input) {
		StringBuilder builder = new StringBuilder();

		for (File f : input.getInputFiles())
				builder.append(FileCommands.dropExtension(f.getName())).append(".o, ");
		String list = builder.toString();
		if (!list.isEmpty())
			list = list.substring(0, list.length() - 2);

		return "Linking object files " + list + "\n";
	}
	@Override
	public File persistentPath(CInput input) {
		return new File(input.getTargetDir(), "linking.c."+ input.getTargetDir().hashCode() +".dep");
	}
	
	public static BuildRequest<CInput, None, CLinkBuilder, BuilderFactory<CInput, None, CLinkBuilder>> request(CInput input) {
		return new BuildRequest<>(factory,input);
	}

	@Override
	protected Stamper defaultStamper() {
		
		return LastModifiedStamper.instance;
	}
	

}
