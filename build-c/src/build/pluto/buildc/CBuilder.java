package build.pluto.buildc;

import java.io.File;
import java.nio.file.StandardCopyOption;

import org.sugarj.common.FileCommands;

import build.pluto.builder.BuildRequest;
import build.pluto.builder.Builder;
import build.pluto.builder.BuilderFactory;
import build.pluto.builder.BuilderFactoryFactory;
import build.pluto.output.None;

public class CBuilder extends Builder<CInput,None> {
	
	public static final BuilderFactory<CInput, None,CBuilder> factory = BuilderFactoryFactory.of(CBuilder.class, CInput.class);

	public CBuilder(CInput input) {
		super(input);
		
	}
	
	@Override
	protected None build(CInput input) throws Throwable {
		if (input.getInputFiles() != null && input.getInputFiles().size() > 0 ) {
			//Hard coded get(0) for single file
			File target = new File(input.getTargetDir().getAbsolutePath()+"\\"+input.getInputFiles().get(0).getName()) ; 
			System.out.println("target path : "+target.getAbsolutePath());
			System.out.println(input.getInputFiles().get(0).getName());
			//require(input.getInputFiles().get(0), LastModifiedStamper.instance); //Null Pointer exceiption 
			FileCommands.copyFile(input.getInputFiles().get(0), target, StandardCopyOption.COPY_ATTRIBUTES);
			//provide(target); //Null pointer but still copying the file
		}
		return None.val;
	}

		public static BuildRequest<CInput, None,CBuilder, BuilderFactory<CInput, None, CBuilder>> request(CInput input) {
			return new BuildRequest<>(factory,input);
		}
	@Override
	protected String description(CInput input) {
		StringBuilder builder = new StringBuilder();
		
		for (File f : input.getInputFiles())
			builder.append(f.getName()).append(", ");
		String list = builder.toString();
		if (!list.isEmpty())
			list = list.substring(0, list.length() - 2);

		return "Compile C files " + list +"\n";
	}

	@Override
	public File persistentPath(CInput input) {
		return new File(input.getTargetDir(), "compile.c."+ input.getInputFiles().hashCode() +".dep");
	}
	
}
