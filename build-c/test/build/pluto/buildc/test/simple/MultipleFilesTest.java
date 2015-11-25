/**
 * 
 */
package build.pluto.buildc.test.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.sugarj.common.FileCommands;

import build.pluto.buildc.CBuilder;
import build.pluto.buildc.CInput;
import build.pluto.buildc.CLinkBuilder;
import build.pluto.buildc.compiler.GCCompiler;
import build.pluto.buildc.compiler.GCLinker;
import build.pluto.builder.BuildManagers;
import build.pluto.test.build.ScopedBuildTest;
import build.pluto.test.build.ScopedPath;
import build.pluto.test.build.TrackingBuildManager;

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

	
	private TrackingBuildManager build() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		ArrayList<File> inputFiles = new ArrayList<>();
		inputFiles.add(sourceFile);
		inputFiles.add(sourceBFile);
		inputFiles.add(sourceCFile);
		manager.require(CLinkBuilder.request(new CInput(inputFiles,targetDir,GCLinker.instance)));
		return manager;
	}
	
	@Test
	public void testBuildClean() throws IOException {
		TrackingBuildManager manager = build();
		assertTrue("No object file for add generated", new File(targetDir, "add.o").exists());
		assertTrue("No object file for foo generated", new File(targetDir, "foo.o").exists());
		assertTrue("No object file for foo1 generated", new File(targetDir, "foo1.o").exists());
		assertTrue("No executable file generated", new File(targetDir, "add.exe").exists());
		assertEquals(2, manager.getExecutedInputs().size());
	}

	@Test
	public void testCleanRebuildDoesNothing() throws IOException {
		build();
		TrackingBuildManager manager = build();
		assertEquals(0, manager.getExecutedInputs().size());
	}

	@Test
	public void testRebuildOnBChange() throws IOException {
		build();
		FileCommands.writeToFile(sourceBFile, "int main() {    int n;    return 0;}");
		TrackingBuildManager manager = build();
		assertEquals("Should compile only foo.c ",1, manager.getExecutedInputs().size());
	}
	@Test
	public void testRebuildAfterChangedSourceFile() throws IOException {
		build();
		FileCommands.writeToFile(sourceFile, " int main() {    int n;    return 0;}");
		TrackingBuildManager manager = build();
		assertEquals("Should compile only add.c ",1, manager.getExecutedInputs().size());
	}
}
