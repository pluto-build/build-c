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
import build.pluto.test.build.ScopedBuildTest;
import build.pluto.test.build.ScopedPath;
import build.pluto.test.build.TrackingBuildManager;

public class SimpleCBuildTest extends ScopedBuildTest {

	@ScopedPath(value = "")
	private File sourcePath;

	@ScopedPath(value = "add.c")
	private File sourceFile;

	@ScopedPath(value = "bin")
	private File targetDir;

	private TrackingBuildManager build() throws IOException {
		TrackingBuildManager manager = new TrackingBuildManager();
		ArrayList<File> files = new ArrayList<>();
		files.add(sourceFile);
		manager.require(CLinkBuilder.request(new CInput(files,targetDir,GCLinker.instance)));
		return manager;
	}
	
	@Test
	public void testBuildClean() throws IOException {
		TrackingBuildManager manager = build();
		assertTrue("No output file generated", new File(targetDir, "add.o").exists());
		assertEquals(manager.getSuccessfullyExecutedInputs().size(), 1);
	}

	@Test
	public void testRebuildAfterCleanDoesNothing() throws IOException {
		build();
		TrackingBuildManager manager = build();
		assertTrue("Nothing is executed", manager.getExecutedInputs().isEmpty());
	}
	
	@Test
	public void testRebuildAfterChangedSourceFile() throws IOException {
		build();
		FileCommands.writeToFile(sourceFile, " int main() {    int n;    return 0;}");
		TrackingBuildManager manager = build();
		System.err.println(manager.getExecutedInputs().size());
		assertEquals(1, manager.getExecutedInputs().size());
	}

	@Test
	public void testRebuildAfterDeletedObjectFile() throws IOException {
		build();
		File classFile = new File(targetDir, "add.o");
		classFile.delete();
		TrackingBuildManager manager = build();
		assertTrue("No class file generated", classFile.exists());
		assertEquals(manager.getExecutedInputs().size(), 1);
	}

}
