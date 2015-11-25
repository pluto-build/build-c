package build.pluto.buildc;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import build.pluto.buildc.compiler.CCompiler;
import build.pluto.buildc.compiler.GCLinker;



public class CInput implements Serializable {

	private static final long serialVersionUID = 6256336102992811823L;
	private final List<File> inputFiles;
	private final File targetDir;
	private final CCompiler compiler;
	private final GCLinker linker;
	
	public CInput(List<File> inputFiles, File targetDir,CCompiler compiler) {
		List<File> absoluteInputFiles = new ArrayList<>(inputFiles.size());
		for (File f : inputFiles)
			absoluteInputFiles.add(f.getAbsoluteFile());
		this.inputFiles = Collections.unmodifiableList(absoluteInputFiles);
		this.targetDir = (targetDir != null ? targetDir : new File(".")).getAbsoluteFile();
		this.compiler=compiler;
		linker = null;
		
	}

	public CInput(List<File> inputFiles, File targetDir,GCLinker linker) {
		List<File> absoluteInputFiles = new ArrayList<>(inputFiles.size());
		for (File f : inputFiles)
			absoluteInputFiles.add(f.getAbsoluteFile());
		this.inputFiles = Collections.unmodifiableList(absoluteInputFiles);
		this.targetDir = (targetDir != null ? targetDir : new File(".")).getAbsoluteFile();
		this.linker=linker;
		compiler = null;
		
	}
	public CInput(File inputFile, File targetDir, File sourcePath,CCompiler compiler) {
		this(Collections.singletonList(inputFile), targetDir,compiler);
	}
	public List<File> getInputFiles() {
		return inputFiles;
	}

	public File getTargetDir() {
		return targetDir;
	}

	public CCompiler getCompiler() {
		return compiler;
	}

	@Override
	public String toString() {
		return "CInput [inputFiles=" + inputFiles + ", targetDir=" + targetDir + "]";
	}

	public GCLinker getLinker() {
		return linker;
	}

	
}
