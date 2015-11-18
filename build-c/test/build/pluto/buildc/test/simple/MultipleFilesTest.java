/**
 * 
 */
package build.pluto.buildc.test.simple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import build.pluto.buildc.CBuilder;
import build.pluto.buildc.CInput;
import build.pluto.buildc.compiler.GCCompiler;
import build.pluto.builder.BuildManagers;
import build.pluto.test.build.ScopedBuildTest;
import build.pluto.test.build.ScopedPath;

/**
 * @author Faisal
 *
 */
public class MultipleFilesTest extends ScopedBuildTest{
	@ScopedPath(value = "")
	private File sourcePath;

	@ScopedPath(value = "add.c")
	private File sourceFile;

	@ScopedPath(value = "foo.c")
	private File sourceBFile;
	@ScopedPath(value = "foo1.c")
	private File sourceCFile;
	@ScopedPath(value = "bin")
	private File targetDir;

	@Test
	public void build() throws IOException {
		ArrayList<File> inputFiles = new ArrayList<>();
		inputFiles.add(sourceFile);
		inputFiles.add(sourceBFile);
		inputFiles.add(sourceCFile);
		BuildManagers.build(CBuilder.request(new CInput(inputFiles, targetDir, GCCompiler.instance)));
		

	}
}
