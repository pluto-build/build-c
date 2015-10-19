package build.pluto.buildc.test.simple;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import build.pluto.buildc.CBuilder;
import build.pluto.buildc.CInput;
import build.pluto.builder.BuildManagers;
import build.pluto.test.build.ScopedBuildTest;
import build.pluto.test.build.ScopedPath;

public class SimpleCBuildTest extends ScopedBuildTest {

	@ScopedPath(value = "")
	private File sourcePath;

	@ScopedPath(value = "add.c")
	private File sourceFile;

	@ScopedPath(value = "bin")
	private File targetDir;

	@Test
	public void build() throws IOException {
		
		BuildManagers.build(CBuilder.request(new CInput(sourceFile, targetDir, sourcePath)));
		
	}

	
}
